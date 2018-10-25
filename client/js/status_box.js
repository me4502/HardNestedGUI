import {API_URL, checkForErrors} from "./util";

const statusBox = document.querySelector("#status-box");
export let lastStatusMessage = 0;

export function addToStatusBox(message) {
    statusBox.textContent += message + "\n";
}

function queryStatuses() {
    window.fetch(API_URL + 'status_messages/' + lastStatusMessage)
        .then(r => r.json())
        .then(json => {
            checkForErrors(json);
            lastStatusMessage = json['message_id'];
            let messagesToAdd = json['messages'];
            for (let message in messagesToAdd) {
                addToStatusBox(messagesToAdd[message]);
            }
        })
        .catch(error => {
            addToStatusBox('Failed to update log: ' + error.message);
        })
}

window.setInterval(queryStatuses, 1000);