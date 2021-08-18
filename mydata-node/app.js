const
    express = require('express'),
    bodyParser = require('body-parser'),
    app = express();
const
    appController = require("./controller/appController");

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use('/', appController)

app.listen(3000);

module.exports = app;
