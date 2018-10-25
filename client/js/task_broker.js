import {API_URL, checkForErrors} from "./util";
import {addToStatusBox} from "./status_box";

let running = false;
const startButton = document.querySelector("#start-button");

export function runApplication() {
    running = true;
    startButton.classList.add('disabled');
    window.fetch(API_URL + 'start_application/', {
        method: 'POST',
        body: {

        },
        headers:{
            'Content-Type': 'application/json'
        }
    })
        .then(r => r.json())
        .then(json => {
            checkForErrors(json);
            addToStatusBox(json['message']);
        })
        .catch(error => {
            addToStatusBox('Failed to start: ' + error.message);
            resetState();
        })
}

function queryStatus() {
    if (!running)
        return;
    window.fetch(API_URL + 'get_application_state/')
        .then(r => r.json())
        .then(json => {
            checkForErrors(json);
        })
        .catch(error => {
            addToStatusBox('Error occurred when querying application: ' + error.message);
            resetState();
        })
}

function resetState() {
    running = false;
    startButton.classList.remove('disabled');
}

setInterval(queryStatus, 2000);