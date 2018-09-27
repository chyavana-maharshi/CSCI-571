
function initAutocomplete2() {

    autocomplete = new google.maps.places.Autocomplete(
        /** @type {!HTMLInputElement} */(document.getElementById('inputFrom')),
        {types: ['geocode']});

}
