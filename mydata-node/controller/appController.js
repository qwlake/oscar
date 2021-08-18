const express = require('express');
const router = express.Router();
const {authorize, tokenFunc, revoke, authenticateRequest} = require("../service/appService");

router.get('/oauth/2.0/authorize', authorize);
router.post('/oauth/2.0/token', tokenFunc);
router.post('/oauth/2.0/revoke', revoke);
router.get('/', authenticateRequest, function(req, res) {
    res.send('Congratulations, you are in a secret area!');
});

module.exports = router;
