export const API_URL = "/";

export function checkForErrors(json) {
    if ("error" in json) {
        throw {"message": json["error"]};
    }
}