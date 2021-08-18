
let config = {
    clients: [{
        id: 'sample-client-id',	// TODO: Needed by refresh_token grant, because there is a bug at line 103 in https://github.com/oauthjs/node-oauth2-server/blob/v3.0.1/lib/grant-types/refresh-token-grant-type.js (used client.id instead of client.clientId)
        clientId: 'sample-client-id',
        clientSecret: 'sample-client-secret',
        grants: [
            'authorization_code',
            'password',
            'refresh_token'
        ],
        redirectUris: ["https://sample.com"]
    }],
    confidentialClients: [
        // {
        //     clientId: 'confidentialApplication',
        //     clientSecret: 'topSecret',
        //     grants: [
        //         'password',
        //         'client_credentials'
        //     ],
        //     redirectUris: []
        // }
    ],
    authorizationCodes: [
        // {
        //     authorizationCode: '',
        //     expiresAt: new Date(),
        //     redirectUri: '',
        //     scope: '',
        //     client: null,
        //     user: null,
        // }
    ],
    tokens: [],
    users: [{
        username: 'pedroetb',
        password: 'password'
    }]
};

/**
 * Dump the memory storage content (for debug).
 */

const dump = function() {
    console.log('clients', config.clients);
    console.log('confidentialClients', config.confidentialClients);
    console.log('tokens', config.tokens);
    console.log('users', config.users);
};

/*
 * Methods used by all grant types.
 */

const getAccessToken = function(token) {

    let tokens = config.tokens.filter(function(savedToken) {
        return savedToken.accessToken === token;
    });

    return tokens[0];
};

const getClient = function(clientId, clientSecret) {

    let clients = config.clients.filter(function(client) {
        return client.clientId === clientId;
    });

    let confidentialClients = config.confidentialClients.filter(function(client) {
        return client.clientId === clientId && client.clientSecret === clientSecret;
    });

    return clients[0] || confidentialClients[0];
};

const saveToken = function(token, client, user) {

    token.client = {
        id: client.clientId
    };

    token.user = {
        username: user.username
    };

    config.tokens.push(token);

    return token;
};

/*
 * Method used only by password grant type.
 */

const getUser = function(username, password) {

    let users = config.users.filter(function(user) {
        return user.username === username && user.password === password;
    });

    return users[0];
};

/*
 * Method used only by client_credentials grant type.
 */

const getUserFromClient = function(client) {

    let clients = config.confidentialClients.filter(function(savedClient) {
        return savedClient.clientId === client.clientId && savedClient.clientSecret === client.clientSecret;
    });

    return clients.length;
};

/*
 * Methods used only by refresh_token grant type.
 */

const getRefreshToken = function(refreshToken) {

    let tokens = config.tokens.filter(function(savedToken) {
        return savedToken.refreshToken === refreshToken;
    });

    if (!tokens.length) {
        return;
    }

    return tokens[0];
};

const revokeToken = function(token) {

    let revokedTokensFound = config.tokens.filter(function(savedToken) {
        return savedToken.refreshToken === token.refreshToken;
    });

    config.tokens = config.tokens.filter(function(savedToken) {
        return savedToken.refreshToken !== token.refreshToken;
    });

    return !!revokedTokensFound.length;
};

/*
 * Methods used only by authorization_code grant type.
 */

const saveAuthorizationCode = function(code, client, user) {
    let authorizationCode = {
        authorizationCode: code.authorizationCode,
        expiresAt: code.expiresAt,
        redirectUri: code.redirectUri,
        scope: code.scope,
        client: client,
        user: user,
    }
    config.authorizationCodes.push(authorizationCode);
    return new Promise(resolve => resolve(Object.assign({
        redirectUri: `${code.redirectUri}`,
    }, authorizationCode)))
};

const getAuthorizationCode = function (authorizationCode) {
    let code = config.authorizationCodes.filter(function(savedCode) {
        return savedCode.authorizationCode === authorizationCode;
    })[0];
    return {
        authorizationCode: code.authorizationCode,
        expiresAt: code.expiresAt,
        redirectUri: code.redirectUri,
        scope: code.scope,
        client: code.client,
        user: code.user
    };
};

const revokeAuthorizationCode = function (code) {
    let revokedCodesFound = config.authorizationCodes.filter(function(savedCode) {
        return savedCode.authorizationCode === code.authorizationCode;
    });

    config.authorizationCodes = config.authorizationCodes.filter(function(savedCode) {
        return savedCode.authorizationCode !== code.authorizationCode;
    });

    return !!revokedCodesFound.length;
};

/*
 * Methods used only by AccessToken revoke.
 */

const revokeTokenByAccessToken = function(token) {
    let revokedTokensFound = config.tokens.filter(function(savedToken) {
        return savedToken.accessToken === token.accessToken;
    });

    config.tokens = config.tokens.filter(function(savedToken) {
        return savedToken.accessToken !== token.accessToken;
    });

    return !!revokedTokensFound.length;
};


module.exports = {
    getAccessToken: getAccessToken,
    getClient: getClient,
    saveToken: saveToken,
    getUser: getUser,
    getUserFromClient: getUserFromClient,
    getRefreshToken: getRefreshToken,
    revokeToken: revokeToken,
    saveAuthorizationCode: saveAuthorizationCode,
    getAuthorizationCode: getAuthorizationCode,
    revokeAuthorizationCode: revokeAuthorizationCode,
    revokeTokenByAccessToken: revokeTokenByAccessToken
};
