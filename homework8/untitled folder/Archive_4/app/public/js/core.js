var app = angular.module('myApp', ['ngAnimate']);


app.controller('myCtrl', function($scope,$http) {
    $scope.distancePH = 10;
    $scope.formData = {keyword:'',category:'default',distance:'',loc:'here',location:'',lat:'',lon:''};
    $scope.loader = {
        loading: false,
    };
    var nextPageToken_1;
    var nextPageToken_2;
    var pageNum = 1;
    var latitude = '';
    var longitude = '';
    $scope.sub=function() {
        if(navigator.onLine){
            $scope.online = true;
            $scope.loader.loading = true;
        var eventLoc = document.getElementById('notHereText').value;
        $scope.formData.location = eventLoc;
        if ($scope.formData.loc === 'here') {
            $http.get("http://ip-api.com/json")
                .then(function (response) {
                    $scope.formData.lat = response.data.lat;
                    $scope.formData.lon = response.data.lon;
                    $scope.lattt = response.data.lat;
                    $scope.lnggg = response.data.lon;
                })
                .then(function (response) {
                    $http.post('/ha', $scope.formData);
                    console.log('posted successfully');
                }).catch(function (response) {
                console.log('error in posting');
            })
                .then(function (response) {
                    $http.get('/list').then(function (response) {
                        $scope.loader.loading = false;
                        $scope.results = response.data.results;
                        $scope.next_page = response.data.next_page_token;
                        nextPageToken_1 = response.data.next_page_token;
                        $scope.pagenum = 1;
                        pageNum = 1;

                        // var fav = JSON.parse(localStorage.getItem('session'));
                        // for(var i=0;i<fav.length;i++){
                        //     var array = $scope.results;
                        //     if(array.some(item => item.place_id === fav[i].place_id)){
                        //         document.getElementById(fav[i].place_id).style.color = 'orange';
                        //         document.getElementById(fav[i].place_id).classList.remove('fa-star-o');
                        //         document.getElementById(fav[i].place_id).classList.add('fa-star');
                        //     }
                        // }

                    })
                })
                .then(function(){

                })
        } else {
            $http.get("https://maps.googleapis.com/maps/api/geocode/json?address=" + $scope.formData.location + "&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo")
                .then(function (response) {
                    $scope.formData.lat = response.data.results[0].geometry.location.lat;
                    $scope.formData.lon = response.data.results[0].geometry.location.lng;
                    $scope.lattt = response.data.lat;
                    $scope.lnggg = response.data.lon;
                })
                .then(function (response) {
                    $http.post('/ha', $scope.formData);
                    console.log('posted successfully');
                }).catch(function (response) {
                console.log('error in posting');
            }).then(function (response) {
                $http.get('/list').then(function (response) {
                    $scope.loader.loading = false;
                    $scope.results = response.data.results;
                    $scope.next_page = response.data.next_page_token;
                    nextPageToken_1 = response.data.next_page_token;
                    $scope.pagenum = 1;
                    pageNum = 1;

                })
            })

        }
    }else{
            $scope.online = false;
            $scope.loader.loading = false;
        }
    };
    $scope.reset = function(){
        $scope.formData = {keyword:'',category:'default',distance:'',loc:'here',location:'',lat:'',lon:''};
    };



    $scope.nextPage=function(){
        if((nextPageToken_1) && (pageNum == 1)){
            $http.get('/next1').then(function(response){
                $scope.results = response.data.results;
                nextPageToken_2 = response.data.next_page_token;
                $scope.next_page = nextPageToken_2;
                pageNum = 2;
                $scope.pagenum = 2;

           })
        }
        else if((nextPageToken_2) && (pageNum == 2)){
            $http.get('/next2').then(function(response){
                $scope.results = response.data.results;
                $scope.next_page = response.data.next_page_token;
                pageNum = 3;
                $scope.pagenum = 3;

                })
        }
    };
    $scope.prePage=function(){
        if(pageNum == 2){
            $http.get('/list').then(function(response){
                $scope.results = response.data.results;
                pageNum = 1;
                $scope.pagenum = 1;
                $scope.next_page = response.data.next_page_token;
            })
        }
        else if(pageNum == 3){
            $http.get('/next1').then(function(response){
                $scope.results = response.data.results;
                pageNum = 2;
                $scope.pagenum = 2;
                $scope.next_page = response.data.next_page_token;
            })
        }
    };
    $scope.showDetail = function (placeid,name,lati,long,wholeResult) {
        $scope.detailName = name;
        $scope.detailId = placeid;
        $scope.wholeResult = wholeResult;
        latitude = lati;
        longitude = long;
        var map = new google.maps.Map(document.getElementById('map666'), {
            center: {lat: lati, lng: long},
            zoom: 13
        });
        var service = new google.maps.places.PlacesService(map);

        service.getDetails({
            placeId: placeid
        }, function(place, status) {
            if (status === google.maps.places.PlacesServiceStatus.OK) {
                var arr = JSON.parse(localStorage.getItem('session'));
                // if(arr.some(item => item.place_id === $scope.detailId)){
                //     document.getElementById(placeid+'1').style.color = 'orange';
                //     document.getElementById(placeid+'1').classList.remove('fa-star-o');
                //     document.getElementById(placeid+'1').classList.add('fa-star');
                // }

                $scope.address = place.formatted_address;
                // document.getElementById('address').innerHTML = place.formatted_address;
                $scope.phone = place.international_phone_number;
                // document.getElementById('phone').innerHTML = place.international_phone_number;
                $scope.price = '$'.repeat(place.price_level);
                // document.getElementById('price').innerHTML = '$'.repeat(place.price_level);
                $scope.rate = place.rating/5*100 + "%";
                $scope.ratin = place.rating;
                $scope.rateNum = place.rating;

                $scope.googlePage = place.url;

                $scope.website = place.website;
                $scope.open_hours = place.opening_hours;
                if(place.opening_hours){
                    $scope.weekdays = place.opening_hours.periods;
                    var utc = place.utc_offset;
                    var nowDate = moment().utcOffset(utc).format('d');
                    $scope.nowDate = nowDate;
                    if(place.opening_hours.periods[0].close){
                        $scope.not24 = true;
                        $scope.nowTime = moment(place.opening_hours.periods[nowDate].open.time, 'hhmm').format('hh:mm A') +
                            ' - ' + moment(place.opening_hours.periods[nowDate].close.time, 'hhmm').format('hh:mm A');
                        if(place.opening_hours.open_now){
                            $scope.days = 'Open now: '+ moment(place.opening_hours.periods[nowDate].open.time, 'hhmm').format('hh:mm A') +
                                ' - ' + moment(place.opening_hours.periods[nowDate].close.time, 'hhmm').format('hh:mm A');
                        }else{
                            $scope.days = 'Closed'
                        }
                    }else if((!place.opening_hours.periods[0].close) && (place.opening_hours.periods[0].open.time='0000')){
                        $scope.not24 = false;
                        if(place.opening_hours.open_now){
                            $scope.days = 'Open now: Open 24 Hours';
                        }else{
                            $scope.days = 'Closed'
                        }
                        $scope.weekdays = [{"d":0},{"d":1},{"d":2},{"d":3},{"d":4},{"d":5},{"d":6}];
                    }

                }
                $scope.showD = function(day){
                    return moment(day, 'd').format('dddd');
                };
                $scope.showT = function(time){
                    return moment(time, 'hhmm').format('hh:mm A');
                };
                if(place.photos) {
                    $scope.photos = place.photos;
                    $scope.photoError = false;
                }else{
                    $scope.photoError = true;
                }

                $scope.getU = function(k){
                  var realU = k.getUrl({"maxWidth": 300});
                  return realU;
                };
                $scope.getOU = function(p,h,w){
                    var str = p.getUrl({"maxWidth": w, "maxHeight": h});
                    return str;
                };

                $scope.showdetail = true;
                $scope.detailError = false;

                if(place.reviews) {
                    $scope.reviewsOrg = place.reviews;
                    $scope.googleReviewError = false;
                }else{
                    $scope.reviewsOrg = '';
                    $scope.googleReviewError = true;
                }


                $scope.$apply();
            }else{
                $scope.detailError = true;
            }
        });


    };


    $scope.initMap = function(){
        document.getElementById('inputFrom').value = '';
        var directionsDisplay = new google.maps.DirectionsRenderer;
        var directionsService = new google.maps.DirectionsService;
        var map = new google.maps.Map(document.getElementById('mapDir'), {
            zoom: 13,
            center: {lat: latitude, lng: longitude}
        });
        var marker = new google.maps.Marker({
            position: {lat: latitude, lng: longitude},
            map: map
        });
        directionsDisplay.setMap(map);
        directionsDisplay.setPanel(document.getElementById('right-panel'));



        var onChangeHandler = function() {
            calculateAndDisplayRoute(directionsService, directionsDisplay);
            marker.setMap(null);
        };
        document.getElementById('getButton').addEventListener('click', onChangeHandler);
        $(window).trigger('resize');
    };

    function calculateAndDisplayRoute(directionsService, directionsDisplay) {
        var start = document.getElementById('inputFrom').value;
        if((start == 'My location')|| !start){
            start = {lat: $scope.lattt, lng: $scope.lnggg};
        }
        var mode = document.getElementById('travelMode').value;
        directionsService.route({
            origin: start,
            destination: $scope.address,
            travelMode: mode,
            provideRouteAlternatives: true
        }, function(response, status) {
            if (status === 'OK') {
                directionsDisplay.setDirections(response);
            } else {
                window.alert('Directions request failed due to ' + status);
            }
        });
    }

    $scope.showStreet = function(){
        var pos = {lat: latitude, lng: longitude};
        var panorama = new google.maps.StreetViewPanorama(
            document.getElementById('mapMan666'), {
                position: {lat: latitude, lng: longitude},
                pov: {
                    heading: 270,
                    pitch: 10
                }
            });
    };
    $scope.getRealTime = function(t){
        var k = moment.unix(t);
        return moment(k,'x').format('YYYY-MM-DD hh:mm:ss');
    };
    $scope.getStar = function(s){
        return  '★'.repeat(s);
    };


    $scope.getYelp=function(){
        var wholeAddress = $scope.address;
        var arr = wholeAddress.split(',');
        if(arr.length == 4){
        $http.post('/yelpmatch',{
            'name0': $scope.detailName,
            'address0': arr[0],
            'city0': arr[1],
            'state0': arr[2].substring(0,2),
        })
        }else if(arr.length == 3){
            $http.post('/yelpmatch',{
                'name0': $scope.detailName,
                'address0': arr[1],
                'city0': arr[0],
                'state0': arr[1].substring(1,3),
            })
        }


    };
    $scope.getYelpReview = function(){
        $http.get('/yelp').then(function(response){
            if(response.data != 'error'){
            $scope.yelpReviews = response.data.reviews;
            $scope.yelpReviewError = false;
            }else{
                $scope.yelpReviews = '';
                $scope.yelpReviewError = true;
            }
        })
    };
    $scope.model = {
        eachDetail: undefined
    };
    $scope.list = {
        listClick : undefined
    };




    $scope.addFav = function(data){

        var sess = JSON.parse(localStorage.getItem('session'));
        if(!sess){
            var a = [];
            sess = data;
            a.push(sess);
            localStorage.setItem('session', JSON.stringify(a));
            console.log('null')
        }else{

            var a = [];
            a = JSON.parse(localStorage.getItem('session'));
            a.push(data);
            localStorage.setItem('session', JSON.stringify(a));
            console.log('not null')
        }
        $scope.favYes = true;
    };

    $scope.fav = function(id){
        if(!JSON.parse(localStorage.getItem('session'))){
            return true;
        }else{
        var favor = JSON.parse(localStorage.getItem('session'));
            if(favor.some(item => item.place_id === id)){
                $scope.favYes = true;
                return false;
            }else{
                $scope.favYes = false;
                return true;
            }
        }
    };
    $scope.detailFav = function(id){
        if(JSON.parse(localStorage.getItem('session'))){
        var favor = JSON.parse(localStorage.getItem('session'));
        if(favor.some(item => item.place_id === id)){
            return false;
        }else{
            return true;
        }
        }
        $scope.favMore();
    };

    $scope.getFav = function(){
        if(JSON.parse(localStorage.getItem('session')) == ''){
            $scope.noFav = true;
        }else{
            $scope.noFav = false;
        if((JSON.parse(localStorage.getItem('session'))).length>20){
            var fav = JSON.parse(localStorage.getItem('session'));
            $scope.favResults = fav.slice(0,20);
            $scope.favMore();
        }else {
            var fr = $scope.favResults;
            $scope.favResults = JSON.parse(localStorage.getItem('session'));
        }
        }
        console.log(fr);
    };
    $scope.removeFav = function(id){

        var array = JSON.parse(localStorage.getItem('session'));
        array = array.filter(function(obj){
            return obj.place_id !== id;
        });
        $scope.favResults = array;

        localStorage.setItem('session', JSON.stringify(array));
        $scope.favYes = false;
        $scope.favMore();
    };
    $scope.favMore = function(){
        var fav = JSON.parse(localStorage.getItem('session'));
        $scope.favResults = fav;
        if(fav.length > 20){
            $scope.more = true;
        }else{
            $scope.more = false;
        }
    };
    $scope.nextFavPage = function(){
        var fav = JSON.parse(localStorage.getItem('session'));
        var len = fav.length;
        $scope.favResults = fav.slice(20,len+1);
    };
    $scope.preFavPage = function(){
        var fav = JSON.parse(localStorage.getItem('session'));
        var len = fav.length;
        $scope.favResults = fav.slice(0,20);
    };
      // localStorage.clear();
    $scope.gg = true;
});


// app.controller('list_1', function($scope,$http){
//
//     $http.get('/list').then(function(response){
//         $scope.city = response.data.results[0].name;
//     })
// });


