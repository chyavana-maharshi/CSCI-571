<?php
error_reporting(E_ERROR | E_PARSE);
foreach($_POST as $key => $value){
    if($key == "keyword"){
        $Keyword = rawurlencode($value);
    }
    elseif ($key == "category"){
        if($value == "default"){
            $value = "";
        }
        $Category = rawurlencode($value);
    }
    elseif ($key == "distance"){
        if($value == ""){
            $value = 16093;
        }else{
            $value = 16093/10 * $value;
        }
        $Distance = $value;
    }
    elseif ($key == "location_button"){
        $Location = rawurlencode($value);
    }
    elseif ($key == "hereLat"){
        $hereLat = $value;
    }
    elseif ($key == "hereLon"){
        $hereLon = $value;
    }
}
if($_POST["location_button"] != "here"){
    $locationURL = "https://maps.googleapis.com/maps/api/geocode/json?address=".$Location."&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";
    $json_location = file_get_contents($locationURL);
    $obj_location = json_decode($json_location,true);
    $locLatitude = $obj_location['results'][0]['geometry']['location']['lat'];
    $locLongitude = $obj_location['results'][0]['geometry']['location']['lng'];
}else{
    $locLatitude = $hereLat;
    $locLongitude = $hereLon;
}
$hereURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=".$locLatitude.",".$locLongitude."&radius=".$Distance."&type=".$Category."&keyword=".$Keyword."&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";
$json_nearby = file_get_contents($hereURL);
$obj_nearby = json_decode($json_nearby,true);
if($obj_nearby['status'] != "INVALID_REQUEST"){
    file_put_contents("jsonNearby.json",$json_nearby);
}
function escapeJsonString($value) { # list from www.json.org: (\b backspace, \f formfeed)
    $escapers = array("\\", "/", "\"", "\n", "\r", "\t", "\x08", "\x0c");
    $replacements = array("\\\\", "\\/", "\\\"", "\\n", "\\r", "\\t", "\\f", "\\b");
    $result = str_replace($escapers, $replacements, $value);
    return $result;
}
?>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Travel and Entertainment Search</title>
    <style>
        h1 {
            font-weight: lighter;
            border-bottom: 2px solid lightgrey;
            text-align: center;
        }
        .formTitle {
            margin-left:10px;
            margin-right:10px;
            margin-top:-20px;
        }
        .formStyle{
            width:600px;
            height:200px;
            border: 4px solid lightgrey;
            text-align: center;
            margin-left: auto;
            margin-right:auto;
            background-color: whitesmoke;
            margin-bottom: 30px;
        }
        form{
            text-align: left;
            line-height: 150%;
            margin-left: 10px;
            margin-top: -10px;
        }
        #locationText{
            margin-left: 309px;
        }
        #submitButton{
            margin-left: 60px;
        }
        table {
            border-collapse:collapse;
            width: 80%;
            margin-left: auto;
            margin-right: auto;
            text-align: center;
        }
        table, td, th {
            border:1px solid lightgray;
        }
        th{
            height: 20px;
            text-align: center;
            padding-left: 10px;
            padding-right: 10px;
        }
        th.ppp{
            padding-top: 10px;
            padding-bottom: 10px;
        }
        td{
            text-align: left;
            height: 40px;
            padding-left: 15px;
        }
        table.emptyTable th{
            background-color: whitesmoke;
            border: 2px solid lightgrey;
            font-weight: lighter;
        }
        #details{
            text-align: center;
            font-weight: bolder;
            margin-bottom: 40px;
        }
        #showReview,#showPhoto{
            text-align: center;
            line-height: 30px;
            cursor: pointer;
            width: 150px;
            margin-left: auto;
            margin-right: auto;
        }

        #review{
            width: 800px;
            margin-right: auto;
            margin-left: auto;
        }
        .author{
            text-align: center;
            font-weight: bolder;
        }
        #noReviews{
            text-align: center;
            font-weight: bold;
            height: 20px;
        }
        #noPhotos{
            text-align: center;
            font-weight: bold;
            height: 20px;
        }
        #photoTable{
            width: 640px;
            text-align: center;
            margin-right: auto;
            margin-left: auto;
        }
        #map0{
            height: 280px;
            width: 300px;
            position: absolute;

        }
        .walk{
            position:absolute;
            height:30px;
            width:80px;
            z-index:50;
            background-color:lightgrey;
            text-align: center;
            padding-top: 5px;
        }
        .bike{
            position:absolute;
            height:30px;
            width:80px;
            z-index:50;
            top: 30px;
            background-color:lightgrey;
            text-align: center;
            padding-top: 5px;
        }
        .drive{
            position:absolute;
            height:30px;
            width:80px;
            z-index:50;
            top:60px;
            background-color:lightgrey;
            text-align: center;
            padding-top: 5px;
        }
        .littleHand{
            cursor: pointer;
        }
        .littleHandA{
            cursor: pointer;
            transition: color 0.5s;
        }
        .littleHandA:hover{
            cursor: pointer;
            color: darkgrey;

        }
        .ppp{
            cursor: pointer;
        }
    </style>
</head>
<body id="pageBody">
<div class="formStyle">
    <div class="formTitle"> <h1><i>Travel and Entertainment Search</i></h1> </div>
    <form method="post" action="<?= $_SERVER['PHP_SELF']; ?>"  id="searchForm">
        <b>Keyword</b><input type="text" name="keyword" id="keyword" required > <br>
        <b>Category</b>
        <select name="category" id="category">
            <option value="default" selected>default</option>
            <option value="cafe">cafe</option>
            <option value="bakery">bakery</option>
            <option value="restaurant">restaurant</option>
            <option value="beauty_salon">beauty salon</option>
            <option value="casino">casino</option>
            <option value="movie_theater">movie theater</option>
            <option value="lodging">lodging</option>
            <option value="airport">airport</option>
            <option value="train_station">train station</option>
            <option value="subway_station">subway station</option>
            <option value="bus_station">bus station</option>
        </select>
        <br>
        <b>Distance (miles)</b><input type="text" name="distance" placeholder="10" id="distance"><b>from</b>
        <input type="radio" name="location_button" value="here" onclick="here()" id="hereButton" >Here <br>
        <input type="radio" name="location_button" value="notHere" onclick="notHere()" id="locationText" ><input type="text" name="location_button" placeholder="location" id="locText" required >
        <br>
        <input type="submit" value="Search" id="submitButton" ><input type="button" value="Clear" onclick="clearAll()">
        <input type="text" name="hereLat" id="hereLat" style="display:none"><input type="text" name="hereLon" id="hereLon" style="display:none">
    </form>
</div>
<div class="tbstyle">
    <p id="tables"></p>
</div>
<p id="details"></p>
<p id="showReview"></p>
<p id="review"></p>
<p id="showPhoto"></p>
<p id="photo"></p>

<script type="text/javascript">

    var xmlhttp;
    document.getElementById("submitButton").disabled = true;
    if (window.XMLHttpRequest)
    {// code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp=new XMLHttpRequest();
    } else {// code for IE6, IE5
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP"); }
    xmlhttp.onreadystatechange=function(){
        if (xmlhttp.readyState == 4 ){
            var jsonObj = JSON.parse(xmlhttp.responseText);
            document.getElementById("hereLon").value = jsonObj.lon;
            document.getElementById("hereLat").value = jsonObj.lat;
            document.getElementById("submitButton").disabled = false;
        }
    };
    xmlhttp.open("POST", "http://ip-api.com/json", true);
    xmlhttp.send();

    <?php
    if(isset($_POST['keyword'])):
    ?>
    document.getElementById("keyword").value = "<?php echo htmlentities($_POST['keyword']); ?>";
    localStorage.setItem('keyword',document.getElementById("keyword").value);
    <?php
    endif;
    ?>
    <?php
    if(isset($_POST['location_button'])&&$_POST['location_button']!="here"):
    ?>
    document.getElementById("locText").value = "<?php echo htmlentities($_POST['location_button']); ?>";
    localStorage.setItem('locText',document.getElementById("locText").value);
    <?php
    endif;
    ?>
    <?php
    if(!isset($_POST['location_button'])):
    ?>
    document.getElementById("hereButton").checked = true;
    <?php
    endif;
    ?>
    <?php
    if(isset($_POST['location_button']) && $_POST['location_button'] == 'here'):
    ?>
    document.getElementById("hereButton").checked = true;
    localStorage.setItem('hereButton',document.getElementById("hereButton").value);
    localStorage.setItem('locationText','');
    <?php
    endif;
    ?>
    <?php
    if(isset($_POST['location_button']) && $_POST['location_button'] != 'here'):
    ?>
    document.getElementById("locationText").checked = true;
    localStorage.setItem('locationText',document.getElementById("locationText").value);
    localStorage.setItem('hereButton','');
    <?php
    endif;
    ?>
    <?php
    if(isset($_POST['category'])):
    ?>
    document.getElementById('category').value = "<?php echo $_POST['category'];?>";
    localStorage.setItem('category',document.getElementById("category").value);
    <?php
    endif;
    ?>
    <?php
    if(isset($_POST['distance'])):
    ?>
    document.getElementById('distance').value = "<?php echo $_POST['distance'];?>";
    localStorage.setItem('distance',document.getElementById("distance").value);
    <?php
    endif;
    ?>
    if(document.getElementById("hereButton").checked){
        document.getElementById("locText").disabled = true;
    }
    if(document.getElementById("locationText").checked){
        document.getElementById("locText").disabled = false;
    }
    function here() {
        document.getElementById("locText").disabled = true;
        document.getElementById("locationText").checked = false;
    }
    function notHere() {
        document.getElementById("locText").disabled = false;
        document.getElementById("hereButton").checked = false;
    }


    function generateTable(){
        html_text = "<table border='1' id='originTable'>";
        html_text += "<tbody>";
        html_text += "<tr>";
        html_text += "<th>Category</th>";
        html_text += "<th>Name</th>";
        html_text += "<th>Address</th>";
        html_text += "</tr>";
        <?php
        for($i=0;$i<count($obj_nearby['results']);$i++):
        ?>
        html_text += "<tr>";
        html_text += "<td><img src=' <?php echo$obj_nearby['results'][$i]['icon'] ?> ' height=40px/></td>";
        html_text += "<td id='<?php echo $i ?>' onclick='loadTable(this);'><a class='littleHand'><?php echo $obj_nearby['results'][$i]['name'] ?></a></td>";
        html_text += "<td><a onclick='initMap(<?php echo $i ?>,<?php echo $obj_nearby['results'][$i]['geometry']['location']['lat'] ?>,<?php echo $obj_nearby['results'][$i]['geometry']['location']['lng'] ?>)' class='littleHandA'><?php echo $obj_nearby['results'][$i]['vicinity']?></a>";
        html_text += "<div id='Bigmap<?php echo $i ?>' style='height:280px;width:300px;position:absolute;down:200px;display:none;'><div onclick='initMap2(<?php echo $i ?>)'  class='walk'><a class='littleHand'>Walk there</a></div>";
        html_text += "<div class='bike' onclick='initMap3(<?php echo $i ?>)' ><a class='littleHand'>Bike there</a></div><div class='drive' onclick='initMap4(<?php echo $i ?>)' ><a class='littleHand'>Drive there</a></div><div id='map<?php echo $i ?>' style='height:280px;width:300px;z-index=5'></div></div></td>";
        html_text += "</tr>";
        <?php
        endfor;
        ?>
        html_text += "</tbody>";
        html_text += "</table>";
        document.getElementById("tables").innerHTML = html_text;

    }
    latt = '<?php echo $locLatitude ?>';
    logg = '<?php echo $locLongitude ?>';
    var uluru;
    var uluru_here;
    function initMap(e,lati,long) {
        uluru = {lat: lati, lng: long};
        uluru_here = {lat: Number(latt), lng: Number(logg)};
        var d = 'Bigmap'+e;
        var k = 'map'+e;
        if(document.getElementById(d).style.display ==="none"){
            document.getElementById(d).style.display = "block";
            var map = new google.maps.Map(document.getElementById(k), {
                zoom: 13,
                center: uluru
            });

            var marker_here = new google.maps.Marker({
                position: uluru,

                map: map
            });
        }else{
            document.getElementById(d).style.display = "none";
        }
    }
    function initMap2(n) {
        var directionsDisplay = new google.maps.DirectionsRenderer;
        var directionsService = new google.maps.DirectionsService;
        var m = 'map'+n;
        var map = new google.maps.Map(document.getElementById(m), {
            zoom: 13,
            center: uluru_here
        });
        directionsDisplay.setMap(map);

        var selectedMode = 'WALKING';
        directionsService.route({
            origin: uluru_here,
            destination: uluru,
            travelMode: google.maps.TravelMode[selectedMode]
        }, function(response, status) {
            if (status == 'OK') {
                directionsDisplay.setDirections(response);
            } else {
                window.alert('Directions request failed due to ' + status);
            }
        });
    }

    function initMap3(n) {
        var directionsDisplay = new google.maps.DirectionsRenderer;
        var directionsService = new google.maps.DirectionsService;
        var m = 'map'+n;
        var map = new google.maps.Map(document.getElementById(m), {
            zoom: 13,
            center: uluru_here
        });
        directionsDisplay.setMap(map);

        var selectedMode = 'BICYCLING';
        directionsService.route({
            origin: uluru_here,
            destination: uluru,

            travelMode: google.maps.TravelMode[selectedMode]
        }, function(response, status) {
            if (status == 'OK') {
                directionsDisplay.setDirections(response);
            } else {
                window.alert('Directions request failed due to ' + status);
            }
        });
    }
    function initMap4(n) {
        var directionsDisplay = new google.maps.DirectionsRenderer;
        var directionsService = new google.maps.DirectionsService;
        var m = 'map'+n;
        var map = new google.maps.Map(document.getElementById(m), {
            zoom: 13,
            center: uluru_here
        });
        directionsDisplay.setMap(map);

        var selectedMode = 'DRIVING';
        directionsService.route({
            origin: uluru_here,
            destination: uluru,
            travelMode: google.maps.TravelMode[selectedMode]
        }, function(response, status) {
            if (status == 'OK') {
                directionsDisplay.setDirections(response);
            } else {
                window.alert('Directions request failed due to ' + status);
            }
        });
    }





    function generateEmpty(){
        empty_text = "<table border='1' class='emptyTable'>";
        empty_text += "<tbody><tr>";
        empty_text += "<th>No Records has been found</th>";
        empty_text += "</tr></tbody></table>";
        document.getElementById("tables").innerHTML = empty_text;
    }
    <?php
    if($_SERVER['REQUEST_METHOD'] == "POST" && !empty($obj_nearby['results'])):
    ?>
    generateTable();
    <?php
    endif;
    ?>
    <?php
    if($_SERVER['REQUEST_METHOD'] == "POST" && empty($obj_nearby['results'])):
    ?>
    generateEmpty();
    <?php
    endif;
    ?>
    function clearAll() {
        document.getElementById("tables").innerHTML = "";
        document.getElementById('searchForm').reset();
        document.getElementById('category').value = "default";
        document.getElementById('hereButton').checked = true;
        document.getElementById('locationText').checked = false;
        document.getElementById('locText').placeholder = "location";
        document.getElementById('locText').value = "";
        document.getElementById('locText').disabled = true;
        document.getElementById('distance').value = "";
        document.getElementById('distance').placeholder = "10";
        document.getElementById('details').innerHTML = "";
        document.getElementById('showReview').innerHTML = "";
        document.getElementById('review').innerHTML = "";
        document.getElementById('showPhoto').innerHTML = "";
        document.getElementById('photo').innerHTML = "";
    }


    function loadTable(s){
        var xmlhttp2;
        if (window.XMLHttpRequest)
        {// code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp2=new XMLHttpRequest();
        } else {// code for IE6, IE5
            xmlhttp2=new ActiveXObject("Microsoft.XMLHTTP"); }
        xmlhttp2.onreadystatechange=function(){
            if (xmlhttp2.readyState == 4 ){

                window.location.href="php999.php?abc="+s.id;
            }
        };
        xmlhttp2.open("GET", "php999.php", true);
        xmlhttp2.send();
    }

    <?php
    if($_SERVER['REQUEST_METHOD'] == "GET" && ($_GET['abc'] != '')):
    ?>

    document.getElementById("keyword").value = localStorage.getItem('keyword');
    document.getElementById('category').value = localStorage.getItem('category');
    document.getElementById('distance').value = localStorage.getItem('distance');

    if(localStorage.getItem('hereButton') == 'here'){
        document.getElementById("hereButton").checked = true;
    }else{document.getElementById("hereButton").checked = false;}


    if(localStorage.getItem('locationText') == "notHere"){
        document.getElementById("locationText").checked = true;
        document.getElementById("locText").value = localStorage.getItem('locText');
        document.getElementById("locText").disabled = false;
    }else{document.getElementById("locationText").checked = false}



    <?php
    $json_nearby1 = file_get_contents("jsonNearby.json") ;
    $obj_nearby1 =  json_decode($json_nearby1,true);
    $placeId = $obj_nearby1['results'][$_GET['abc']]['place_id'];
    $detailURL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" . $placeId . "&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";
    $json_detail = file_get_contents($detailURL);
    $obj_detail = json_decode($json_detail, true);
    ?>

    detailText = "<?php echo escapeJsonString($obj_detail['result']['name']) ?>";

    document.getElementById('details').innerHTML = detailText;
    document.getElementById('tables').innerHTML = "";
    showR = "click to show reviews<br>";
    showR += "<img src='http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png' height='20px' id='showReviewArrow'>";
    document.getElementById("showReview").innerHTML = showR;
    document.getElementById("review").style.display = "none";
    document.getElementById('showReview').onclick = function () {
        document.getElementById("photo").style.display = "none";
        if (document.getElementById("review").style.display === "none") {

            hideR = "click to hide reviews<br>";
            hideR += "<img src='http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png' height='20px' id='closeReviewArrow'>";
            document.getElementById("showReview").innerHTML = hideR;

            reviewT = "<table border='1'>";
            reviewT += "<tbody id='auuu'>";
            <?php
            if(isset($obj_detail['result']['reviews'])):
            ?>
            <?php
            for($n = 0;$n < 5;$n++):
            ?>
            <?php
            if(isset($obj_detail['result']['reviews'][$n]['text']) && isset($obj_detail['result']['reviews'][$n]['profile_photo_url'])) :
            ?>
            reviewT += "<tr>";
            reviewT += "<td class='author'><img src = '<?php echo $obj_detail['result']['reviews'][$n]['profile_photo_url'] ?>'height='30px'> <?php echo $obj_detail['result']['reviews'][$n]['author_name'] ?></td>";
            reviewT += "</tr>";
            reviewT += "<tr>";
            reviewT += "<td class='author_text'><?php echo escapeJsonString($obj_detail['result']['reviews'][$n]['text']) ?></td>";
            reviewT += "</tr>";
            <?php
            endif;
            ?>
            <?php
            endfor;
            ?>
            <?php
            else:
            ?>
            reviewT += "<tr>";
            reviewT += "<th id='noReviews'>No Reviews Found</th>";
            reviewT += "</tr>";
            <?php
            endif;
            ?>

            reviewT += "</tbody>";
            reviewT += "</table>";
            document.getElementById("review").innerHTML = reviewT;
            document.getElementById("review").style.display = "block";
        }
        else {
            document.getElementById("review").style.display = "none";
            document.getElementById("showReview").innerHTML = showR;
        }

        showP = "click to show photos<br>";
        showP += "<img src='http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png' height='20px' id='showPhotoArrow'>";
        document.getElementById("showPhoto").innerHTML = showP;
    };

    <?php
    for($k=0;$k<5;$k++){
        $photo_ref = $obj_detail['result']['photos'][$k]['photo_reference'];
        $photo_url = 'https://maps.googleapis.com/maps/api/place/photo?maxwidth=750&photoreference='.$photo_ref.'&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo';
        $photo = file_get_contents($photo_url);
        file_put_contents($k.'.jpg', $photo);
    }
    ?>

    showP = "click to show photos<br>";
    showP += "<img src='http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png' height='20px' id='showPhotoArrow' >";
    document.getElementById("showPhoto").innerHTML = showP;
    document.getElementById("photo").style.display = "none";
    document.getElementById('showPhoto').onclick = function () {
        document.getElementById("review").style.display = "none";
        showR = "click to show reviews<br>";
        showR += "<img src='http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png' height='20px' id='showReviewArrow'>";
        document.getElementById("showReview").innerHTML = showR;
        if (document.getElementById("photo").style.display === "none") {
            hideP = "click to hide photos<br>";
            hideP += "<img src='http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png' height='20px' id='closeReviewArrow'>";
            document.getElementById("showPhoto").innerHTML = hideP;

            photoT = "<table border='1' id='photoTable'>";
            photoT += "<tbody >";
            <?php
            if(isset($obj_detail['result']['photos'])):
            ?>
            <?php
            for($m = 0;$m < 5;$m++):
            ?>
            <?php
            if(isset($obj_detail['result']['photos'][$m]['photo_reference'])) :
            ?>
            photoT += "<tr><th class='ppp' onclick = window.open('<?php echo $m.'.jpg' ?>')>";
            photoT += "<img src='<?php echo $m.'.jpg' ?>'>";
            photoT += "</th></tr>";
            <?php
            endif;
            ?>
            <?php
            endfor;
            ?>
            <?php
            else:
            ?>
            photoT += "<tr>";
            photoT += "<th id='noPhotos'>No Photos Found</th>";
            photoT += "</tr>";
            <?php
            endif;
            ?>

            photoT += "</tbody>";
            photoT += "</table>";
            document.getElementById("photo").innerHTML = photoT;
            document.getElementById("photo").style.display = "block";
        }
        else {
            document.getElementById("photo").style.display = "none";
            document.getElementById("showPhoto").innerHTML = showP;
        }


    };


    <?php
    endif;
    ?>

</script>
<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo">
</script>


</body>
</html>