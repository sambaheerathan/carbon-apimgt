//noinspection JSUnusedGlobalSymbols
function onRequest(env) {
    sendToClient("swaggerURL", env.config.swaggerURL);
    return {"applicationId":env.pathParams['id']};
}

