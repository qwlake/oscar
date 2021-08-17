
const
    express = require('express'),
    bodyParser = require('body-parser'),
    OAuth2Server = require('oauth2-server'),
    Request = OAuth2Server.Request,
    Response = OAuth2Server.Response;
const {InvalidRequestError} = require("oauth2-server");
const {revokeTokenByAccessToken} = require("./model");

const app = express();

app.use(bodyParser.urlencoded({ extended: true }));

app.use(bodyParser.json());

app.oauth = new OAuth2Server({
    model: require('./model.js'),
    accessTokenLifetime: 60 * 60 * 24,
    allowBearerTokensInQueryString: true
});

app.get('/oauth/2.0/authorize', authorize);
app.post('/oauth/2.0/token', tokenFunc);
app.post('/oauth/2.0/revoke', revoke);
app.get('/', authenticateRequest, function(req, res) {
    res.send('Congratulations, you are in a secret area!');
});

app.listen(3000);


function validate_clientId_and_redirectUri(info) {
    if (info.client_id === undefined
        // todo client_id 유효성 검증 필요 (DB)
    ) {
        throw new InvalidRequestError('invalid_client_id');
    }
    if (info.redirect_uri === undefined
        // todo redirect_uri 유효성 검증 필요 (DB)
    ) {
        throw new InvalidRequestError('invalid_redirection');
    }
    return info
}

function validate_authorize_request(info) {
    if (info.user_ci === undefined ||
        info.api_tran_id === undefined ||
        info.org_code === undefined ||
        info.app_scheme === undefined ||
        info.state === undefined) {
        throw new InvalidRequestError('invalid_request_param');
    }

    // todo unauthorized_client 검증
    // todo access_denied 검증
    // todo unsupported_response_type 검증
    // todo server_error 검증
    // todo temporarily_unavailable 검증

    return info
}

function validate_token_revoke_request(info) {
    if (info.api_tran_id === undefined ||
        info.org_code === undefined ||
        info.client_id === undefined ||
        info.client_secret === undefined
    ) {
        throw new InvalidRequestError('invalid_request_param');
    }

    // todo invalid_client 검증
    // todo invalid_grant 검증
    // todo unauthorized_client 검증
    // todo invalid_scope 검증

    return info
}

function validate_token_issue_request(info) {
    if (info.api_tran_id === undefined ||
        info.org_code === undefined ||
        info.code === undefined ||
        info.client_id === undefined ||
        info.client_secret === undefined ||
        info.redirect_uri === undefined
    ) {
        throw new InvalidRequestError('invalid_request_param');
    }

    // todo invalid_client 검증
    // todo invalid_grant 검증
    // todo unauthorized_client 검증
    // todo invalid_scope 검증

    return info
}

function validate_token_refresh_request(info) {
    if (info.api_tran_id === undefined ||
        info.org_code === undefined ||
        info.refresh_token === undefined ||
        info.client_id === undefined ||
        info.client_secret === undefined
    ) {
        throw new InvalidRequestError('invalid_request_param');
    }

    // todo invalid_client 검증
    // todo invalid_grant 검증
    // todo unauthorized_client 검증
    // todo invalid_scope 검증

    return info
}

function authorize(req, res) {

    const request = new Request(req);
    const response = new Response(res);

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

    // Validation result -> error. http code 400
    try {
        request.info = validate_clientId_and_redirectUri(request.info)
    } catch (err) {
        return res.status(err.code || 400).json({
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
    if (req.body.grant_type === 'authorization_code') {
        return _token_issue(req, res)
    } else if (req.body.grant_type === 'refresh_token') {
        return _token_refresh(req, res)
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

function _token_issue(req, res) {

    const request = new Request(req);
    const response = new Response(res);

    request.info = {
        api_tran_id: req.get("x-api-tran-id"),
        org_code: req.body.org_code,
        grant_type: req.body.grant_type,
        code: req.body.code,
        client_id: req.body.client_id,
        client_secret: req.body.client_secret,
        redirect_uri: req.body.redirect_uri
    };

    try {
        request.info = validate_token_issue_request(request.info)
    } catch (err) {
        return res.status(err.code || 400)
            .header('x-api-tran-id', request.info.api_tran_id)
            .json({
                error: err.name,
                error_description: err.message
            });
    }

    return app.oauth.token(request, response)
        .then(function(token) {
            res.header('x-api-tran-id', request.info.api_tran_id)
            res.json({
                token_type: 'Bearer',
                access_token: token.accessToken,
                expires_in: token.accessTokenExpiresAt,
                refresh_token: token.refreshToken,
                refresh_token_expires_in: token.refreshTokenExpiresAt,
                scope: 'insu.list insu.insurance insu.loan insu.irp'    // todo scope 정리 필요. 현재는 모든 scope 전달중.
            });
        }).catch(function(err) {
            res.status(err.code || 503)
                .header('x-api-tran-id', request.info.api_tran_id)
                .header('Retry-After', 30)
                .json({
                    error: err.name,
                    error_description: err.message,
                });
        });
}

function _token_refresh(req, res) {

    const request = new Request(req);
    const response = new Response(res);

    request.info = {
        api_tran_id: req.get("x-api-tran-id"),
        org_code: req.body.org_code,
        grant_type: req.body.grant_type,
        refresh_token: req.body.refresh_token,
        client_id: req.body.client_id,
        client_secret: req.body.client_secret
    };

    try {
        request.info = validate_token_refresh_request(request.info)
    } catch (err) {
        return res.status(err.code || 400)
            .header('x-api-tran-id', request.info.api_tran_id)
            .json({
                error: err.name,
                error_description: err.message
            });
    }

    return app.oauth.token(request, response, {alwaysIssueNewRefreshToken: false})
        .then(function(token) {
            res.header('x-api-tran-id', request.info.api_tran_id)
            res.json({
                token_type: 'Bearer',
                access_token: token.accessToken,
                expires_in: token.accessTokenExpiresAt
            });
        }).catch(function(err) {
            res.status(err.code || 503)
                .header('x-api-tran-id', request.info.api_tran_id)
                .header('Retry-After', 30)
                .json({
                    error: err.name,
                    error_description: err.message,
                });
        });
}

function revoke(req, res) {

    const request = new Request(req);
    const response = new Response(res);

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
        return res.status(err.code || 400)
            .header('x-api-tran-id', request.info.api_tran_id)
            .json({
                error: err.name,
                error_description: err.message
            });
    }

    try {
        const rsp_code = revokeTokenByAccessToken({
            accessToken: request.info.token
        })? '00000': '99999';
        return res
            .header('x-api-tran-id', request.info.api_tran_id)
            .json({
                rsp_code: rsp_code,
                rsp_msg: ''
            });
    } catch (err) {
        return res.status(err.code || 503)
            .header('x-api-tran-id', request.info.api_tran_id)
            .header('Retry-After', 30)
            .json({
                error: err.name,
                error_description: err.message,
            });
    }
}

function authenticateRequest(req, res, next) {

    const request = new Request(req);
    const response = new Response(res);

    return app.oauth.authenticate(request, response)
        .then(function(token) {
            // todo token verification.
            next();
        }).catch(function(err) {
            res.status(err.code || 500).json(err);
        });
}
