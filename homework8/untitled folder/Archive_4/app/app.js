var express = require('express');
var reload = require('reload');
var app = express();
var bodyParser = require('body-parser');
var moment = require('moment');

app.set('port', process.env.PORT || 3000 );

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static('app/public'));
app.use(require('./routes/index'));
app.use(require('./routes/text'));



app.listen(app.get('port'), function() {
    console.log('Listening on port ' + app.get('port'));
});

reload(app);