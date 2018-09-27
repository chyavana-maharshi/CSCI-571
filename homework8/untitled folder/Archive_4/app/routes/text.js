var express = require('express');
var router = express.Router();
var request = require('request');


var listURL='';
router.post('/ha',function(req,res){
    if(req.body.distance === ''){
        req.body.distance = 10;
    }
    req.body.distance = req.body.distance * 16093/10;
    listURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+req.body.lat+","+
        req.body.lon+"&radius="+req.body.distance+"&type="+req.body.category+"&keyword="+
        req.body.keyword+"&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";
    console.log(req.body);
    console.log(listURL);

    res.end();
});

var nextPageToken_1 = '';
var nextPageToken_2 = '';
router.get('/list',function(req,res){
    request(listURL, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var importedJSON = JSON.parse(body);
            res.send(importedJSON);
            nextPageToken_1 = importedJSON.next_page_token;
            // console.log(importedJSON.next_page_token);
        }
    });

});

router.get('/next1',function(req,res){
    var nextURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken="+nextPageToken_1+"&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";
    request(nextURL, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var importedJSON = JSON.parse(body);
            res.send(importedJSON);
            nextPageToken_2 = importedJSON.next_page_token;

        }
    });

});

router.get('/next2',function(req,res){
    var nextURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken="+nextPageToken_2+"&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";
    request(nextURL, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var importedJSON = JSON.parse(body);
            res.send(importedJSON);

        }
    });

});

'use strict';

const yelp = require('yelp-fusion');

const client = yelp.client('0iYP40UBGzAUVUQREUuSiRvHiZXPwXwVrJJBCAOBZkTk53sjqAiIKsDqLLnitUAF1Zdfeb3fSCmMHNNI8huBEauJK1ae04YxuO7OfP2l56wpfCH7IOF9Sl469wbDWnYx');

var yelpId;

router.post('/yelpmatch',function(req,res){
    console.log(req.body);

// matchType can be 'lookup' or 'best'
    client.businessMatch('best', {
        name: req.body.name0,
        address1: req.body.address0,
        city: req.body.city0,
        state: req.body.state0,
        country: 'US'
    }).then(response => {
        console.log(response.jsonBody.businesses[0].id);
        yelpId = response.jsonBody.businesses[0].id
    }).catch(e => {
        console.log(e);
        yelpId='';
    });
});

router.get('/yelp',function(req,res){

    client.reviews(yelpId).then(response => {
        res.send(response.jsonBody);
    }).catch(e => {
        console.log(e);
        res.send('error');
    });

});

// router.get('/pre1',function(req,res){
//     var preURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken="+nextPageUrl+"&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";
//     request(preURL, function (error, response, body) {
//         if (!error && response.statusCode == 200) {
//             var importedJSON = JSON.parse(body);
//             res.send(importedJSON);
//             nextPageToken = importedJSON.next_page_token;
//         }
//     });
//
// });


module.exports = router;