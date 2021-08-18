const
    OAuth2Server = require('oauth2-server'),
    Request = OAuth2Server.Request,
    Response = OAuth2Server.Response;
const {
    validate_clientId_and_redirectUri,
    validate_authorize_request,
    validate_token_issue_request,
    validate_token_refresh_request,
    validate_token_revoke_request
} = require("./validationService");
const {revokeTokenByAccessToken} = require("../model/model");
const app = require("../app");

app.oauth = new OAuth2Server({
    model: require('../model/model.js'),
    accessTokenLifetime: 60 * 60 * 24,
    allowBearerTokensInQueryString: true
});

function authorize(req, res) {

    let request = new Request(req);
    let response = new Response(res);

    request.info = {
        user_ci: req.get("x-user-ci"),
        api_tran_id: req.get("x-api-tran-id"),
        org_code: req.query.org_code,
        response_type: req.query.response_type,
        client_id: req.query.client_id,
        redirect_uri: req.query.redirect_uri,
        app_scheme: req.query.app_scheme,
        state: req.query.state
    };
    request.body.scope = 'insu.list insu.insurance insu.loan insu.irp'    // todo scope 정리 필요. 현재는 모든 scope 전달중.

    // Validation result -> error. http code 400
    try {
        request.info = validate_clientId_and_redirectUri(request.info)
    } catch (err) {
        return res
            .status(err.code || 400)
            .json({
                error: err.name,
                error_description: err.message,
                state: request.info.state,
                api_tran_id: request.info.api_tran_id
            });
    }

    // Validation result -> error. http code 302
    try {
        request.info = validate_authorize_request(request.info)
    } catch (err) {
        return res.redirect(`${request.info.redirect_uri}/?error=${err.name}&error_description=${err.message}&state=${request.info.state}&api_tran_id=${request.info.api_tran_id}`);
    }

    // Normal process
    return app.oauth.authorize(request, response, {
        authenticateHandler:{
            handle: function(request, response) {
                return {
                    username: request.info.user_ci
                }
            }
        }
    })
        .then(function(token) {
            res.redirect(`${request.info.redirect_uri}/?code=${token.authorizationCode}&state=${request.info.state}&api_tran_id=${request.info.api_tran_id}`);
        }).catch(function(err) {
            res.status(err.code || 500).json(err);
        });
}

function tokenFunc(req, res) {
    if (req.body.grant_type === 'authorization_code' || req.body.grant_type === 'refresh_token') {
        let request = new Request(req);
        let response = new Response(res);
        let response_model;
        let options = {};

        try {
            if (req.body.grant_type === 'authorization_code') {
                request.info = {
                    api_tran_id: req.get("x-api-tran-id"),
                    org_code: req.body.org_code,
                    grant_type: req.body.grant_type,
                    code: req.body.code,
                    client_id: req.body.client_id,
                    client_secret: req.body.client_secret,
                    redirect_uri: req.body.redirect_uri
                };
                request.info = validate_token_issue_request(request.info)
                response_model = function (token) {
                    return {
                        token_type: 'Bearer',
                        access_token: token.accessToken,
                        expires_in: token.accessTokenExpiresAt,
                        refresh_token: token.refreshToken,
                        refresh_token_expires_in: token.refreshTokenExpiresAt,
                        scope: token.scope
                    }
                }
            } else {
                request.info = {
                    api_tran_id: req.get("x-api-tran-id"),
                    org_code: req.body.org_code,
                    grant_type: req.body.grant_type,
                    refresh_token: req.body.refresh_token,
                    client_id: req.body.client_id,
                    client_secret: req.body.client_secret
                };
                request.info = validate_token_refresh_request(request.info)
                response_model = function (token) {
                    return {
                        token_type: 'Bearer',
                        access_token: token.accessToken,
                        expires_in: token.accessTokenExpiresAt
                    }
                }
                options['alwaysIssueNewRefreshToken'] = false
            }
            return _token(request, response, res, response_model, options);
        } catch (err) {
            return res
                .status(err.code || 400)
                .header('x-api-tran-id', request.info.api_tran_id)
                .json({
                    error: err.name,
                    error_description: err.message
                });
        }

    } else {
        return res
            .status(400)
            .header('x-api-tran-id', req.get("x-api-tran-id"))
            .json({
                error: 'unsupported_grant_type',
                error_description: 'unsupported_grant_type',
            });
    }
}

function _token(request, response, res, response_model, options={}) {
    return app.oauth.token(request, response, options)
        .then(function(token) {
            res.header('x-api-tran-id', request.info.api_tran_id)
            res.json(response_model(token));
        }).catch(function(err) {
            res
                .status(err.code || 503)
                .header('x-api-tran-id', request.info.api_tran_id)
                .header('Retry-After', 30)
                .json({
                    error: err.name,
                    error_description: err.message,
                });
        });
}

function revoke(req, res) {

    let request = new Request(req);
    let response = new Response(res);

    request.info = {
        api_tran_id: req.get("x-api-tran-id"),
        org_code: req.body.org_code,
        token: req.body.token,
        client_id: req.body.client_id,
        client_secret: req.body.client_secret
    };

    try {
        request.info = validate_token_revoke_request(request.info)
    } catch (err) {
        return res
            .status(err.code || 400)
            .header('x-api-tran-id', request.info.api_tran_id)
            .json({
                error: err.name,
                error_description: err.message
            });
    }

    try {
        let rsp_code, rsp_msg;
        if (revokeTokenByAccessToken({
            accessToken: request.info.token
        })) {
            rsp_code = '00000';
            rsp_msg = 'revoke_success'
        } else {
            rsp_code = '99999';
            rsp_msg = 'revoke_failed'
        }
        return res
            .header('x-api-tran-id', request.info.api_tran_id)
            .json({
                rsp_code: rsp_code,
                rsp_msg: rsp_msg
            });
    } catch (err) {
        return res
            .status(err.code || 503)
            .header('x-api-tran-id', request.info.api_tran_id)
            .header('Retry-After', 30)
            .json({
                error: err.name,
                error_description: err.message,
            });
    }
}

function authenticateRequest(req, res, next) {

    let request = new Request(req);
    let response = new Response(res);

    return app.oauth.authenticate(request, response)
        .then(function(token) {
            // todo token verification.
            next();
        }).catch(function(err) {
            res.status(err.code || 500).json(err);
        });
}


module.exports = {
    authorize: authorize,
    tokenFunc: tokenFunc,
    revoke: revoke,
    authenticateRequest: authenticateRequest
};
