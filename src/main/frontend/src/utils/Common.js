export const BASE_URL = "http://localhost:8080"

export const getAccessToken = () => {
    return localStorage.getItem("access_token");
 }

 export const getRefreshToken = () => {
    return localStorage.getItem("refresh_token");
 }

 export const getEmail = () => {
    return localStorage.getItem("email");
 }

 export const getName = () => {
    return localStorage.getItem("name");
 }

 export const getLastName = () => {
    return localStorage.getItem("lastname");
 }

 export const getOrganizationName = () => {
    console.log('Getting org name', localStorage.getItem("orgName"));
    return localStorage.getItem("orgName");
 }

 export const saveTokens = (accessToken, refreshToken) => {
    localStorage.setItem("access_token", accessToken);
    localStorage.setItem("refresh_token", refreshToken);
 }

 export const saveEmail = (email) => {
    localStorage.setItem("email", email);
 }

 export const savePersonalData = (name, lastName, organizationName) => {
    localStorage.setItem("name", name);
    localStorage.setItem("lastname", lastName);
    localStorage.setItem("orgName", organizationName);
}

 export const removeTokens = () => {
    localStorage.removeItem("access_token");
    localStorage.removeItem("refresh_token");
 }

export const removeEmail = () => {
    localStorage.removeItem("email")
}

export const removePersonalData = () => {
    localStorage.removeItem("name");
    localStorage.removeItem("lastname");
    localStorage.removeItem("orgName");
}