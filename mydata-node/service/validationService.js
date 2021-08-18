const {InvalidRequestError} = require("oauth2-server");


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


module.exports = {
    validate_clientId_and_redirectUri: validate_clientId_and_redirectUri,
    validate_authorize_request: validate_authorize_request,
    validate_token_revoke_request: validate_token_revoke_request,
    validate_token_issue_request: validate_token_issue_request,
    validate_token_refresh_request: validate_token_refresh_request
};
