import {API_URL, checkForErrors} from "./util";
import {addToStatusBox} from "./status_box";
import {getGridKeys, GRID_IDs, setGridKey} from "./sector_grid";

let running = false;
const startButton = document.querySelector("#start-button");

export function runApplication() {
    disableUI();
    window.fetch(API_URL + 'start_application/', {
        method: 'POST',
        body: JSON.stringify({
            "keys": getGridKeys()
        }),
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
            if ("update" in json) {
                let updates = json["update"];
                for (let key in updates) {
                    setGridKey(key, updates[key]);
                }
            }
            if ("failMessage" in json) {
                addToStatusBox("Run failed: " + json['failMessage']);
                resetState();
            }
        })
        .catch(error => {
            addToStatusBox('Error occurred when querying application: ' + error.message);
            resetState();
        })
}

function disableUI() {
    running = true;
    startButton.classList.add('disabled');
    for (let id in GRID_IDs) {
        document.getElementById(GRID_IDs[id]).disabled = true;
    }
}

function resetState() {
    running = false;
    startButton.classList.remove('disabled');
    for (let id in GRID_IDs) {
        document.getElementById(GRID_IDs[id]).disabled = false;
    }
}

setInterval(queryStatus, 2000);