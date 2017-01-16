var AppkeyTemplate, client;
var grantTypes = {
    "0": {"key": "refresh_token", "grantType": "Refresh Token"},
    "1": {"key": "urn:ietf:params:oauth:grant-type:saml2-bearer", "grantType": "SAML2"},
    "2": {"key": "implicit", "grantType": "Implicit"},
    "3": {"key": "password", "grantType": "Password"},
    "4": {"key": "iwa:ntlm", "grantType": "IWA-NTLM"},
    "5": {"key": "client_credentials", "grantType": "Client Credential"},
    "6": {"key": "authorization_code", "grantType": "Code"}
};

$(function () {

    $('.selectpicker').selectpicker({
        style: 'btn-info',
        size: 4
    });

    $(".navigation ul li.active").removeClass('active');
    var prev = $(".navigation ul li:first")
    $(".green").insertBefore(prev).css('top','0px').addClass('active');

    client = new SwaggerClient({
        url: swaggerURL + "applications",
        success: function () {
            client.setBasePath("");
            var id = document.getElementById("appid").value;
            client.clientAuthorizations.add("apiKey", new SwaggerClient.ApiKeyAuthorization("Authorization", "Basic YWRtaW46YWRtaW4=", "header"));
            client.default.applicationsApplicationIdGet({"applicationId": id},
                function (data) {
                    renderAppDetails(data);
                    renderApplicationKeys(data);
                });
        },
        error: function (e) {
            alert("Error occurred while creating client");
        }
    });

    var renderAppDetails = function (data) {

        $.ajax({
            url: '/store/public/components/root/base/templates/applications/appDetails.hbs',
            type: 'GET',
            success: function (result) {
                var templateScript = result;
                var template = Handlebars.compile(templateScript);

                var context = {
                    "name": data.obj.name,
                    "tier": data.obj.throttlingTier,
                    "status": data.obj.status,
                    "description": data.obj.description
                };
                var compiledHtml = template(context);
                $("#details").append(compiledHtml);
            },
            error: function (e) {
                alert("Error occurred while viewing application details");
            }
        });
    };

    var renderApplicationKeys = function (data) {

        $.ajax({
            url: '/store/public/components/root/base/templates/applications/applicationKeys.hbs',
            type: 'GET',
            success: function (result) {
                var keyTemplateScript = result;
                AppkeyTemplate = Handlebars.compile(keyTemplateScript);
                var compiledHtml, context;

                if (typeof data.obj.keys[0] !== 'undefined') {

                    for (var i = 0; i < data.obj.keys.length; i++) {
                        var keyType = data.obj.keys[i].keyType;

                        for (var j = 0; j < Object.keys(grantTypes).length; j++) {
                            if ((data.obj.callbackUrl == undefined || data.obj.callbackUrl == "" ) &&
                                (grantTypes[j].key == "authorization_code" || grantTypes[j].key == "implicit")) {
                                grantTypes[j].selected = false;
                                grantTypes[j].disabled = true;
                            } else {
                                //TODO check with supportedGrantTypes
                                grantTypes[j].selected = true;
                                grantTypes[j].disabled = false;
                            }
                        }

                        context = {
                            "callbackUrl": data.obj.callbackUrl,
                            "grantTypes": grantTypes,
                            "name": data.obj.name,
                            "show_keys": false,
                            "Key": data.obj.keys[i].token.accessToken,
                            "ConsumerKey": data.obj.keys[i].consumerKey,
                            "ConsumerSecret": data.obj.keys[i].consumerSecret,
                            "username": "Username",
                            "password": "Password",
                            "basickey": window.btoa(data.obj.keys[i].consumerKey + ":" + data.obj.keys[i].consumerSecret),
                            "ValidityTime": data.obj.keys[i].token.validityTime,
                            "Scopes": "",
                            "tokenScopes": data.obj.keys[i].token.tokenScopes,
                            "provide_keys_form": false,
                            "provide_keys": false,
                            "gatewayurlendpoint": "(gatewayurl)/token"

                        };
                        compiledHtml = AppkeyTemplate(context);
                        if (keyType.toLowerCase() == "production") {
                            $("#production").append(compiledHtml);

                            if (data.obj.keys.length == 1) {
                                context = setDefaultContext(data);
                                compiledHtml = AppkeyTemplate(context);
                                $("#sandbox").append(compiledHtml);
                            }
                        }
                        else {
                            $("#sandbox").append(compiledHtml);

                            if (data.obj.keys.length == 1) {
                                context = setDefaultContext(data);
                                compiledHtml = AppkeyTemplate(context);
                                $("#production").append(compiledHtml);
                            }
                        }
                        $('.selectpicker').selectpicker('refresh');
                    }
                } else {
                    context = setDefaultContext(data);
                    compiledHtml = AppkeyTemplate(context);
                    $("#production").append(compiledHtml);
                    $("#sandbox").append(compiledHtml);
                    $('.selectpicker').selectpicker('refresh');
                }
                
                var ClipboardClient = new ZeroClipboard($('.copy-button'));

                ClipboardClient.on('ready', function (event) {
                    ClipboardClient.on('copy', function (event) {
                        event.clipboardData.setData('text/plain', event.target.value);
                    });
                });

                ClipboardClient.on('error', function (event) {
                    alert('ZeroClipboard error of type "' + event.name + '": ' + event.message);
                    ZeroClipboard.destroy();
                });
            },
            error: function (e) {
                alert("Error occurred viewing application details");
            }
        });
    };

    var setDefaultContext = function (data) {

        var ifCreatedKeystate, ifRejectedKeystate, ifCompletedKeystate, keyState;

        for (var j = 0; j < Object.keys(grantTypes).length; j++) {
            if ((data.obj.callbackUrl == undefined || data.obj.callbackUrl == "" ) &&
                (grantTypes[j].key == "authorization_code" || grantTypes[j].key == "implicit")) {
                grantTypes[j].selected = false;
                grantTypes[j].disabled = true;
            } else {
                grantTypes[j].selected = true;
                grantTypes[j].disabled = false;
            }
        }

        if (data.obj.status == 'CREATED') {
            keyState = true;
            ifCreatedKeystate = true;
            ifRejectedKeystate = false;
            ifCompletedKeystate = false;
        }
        else if (data.obj.status == 'REJECTED') {
            keyState = true;
            ifCreatedKeystate = false;
            ifRejectedKeystate = true;
            ifCompletedKeystate = false;
        }
        else if (data.obj.status == 'COMPLETED') {
            keyState = true;
            ifCreatedKeystate = false;
            ifRejectedKeystate = false;
            ifCompletedKeystate = true;
        }
        else {
            keyState = false;
            ifCreatedKeystate = false;
            ifRejectedKeystate = false;
            ifCompletedKeystate = false;
        }

        var context = {
            "callbackUrl": data.obj.callbackUrl,
            "grantTypes": grantTypes,
            "name": data.obj.name,
            "keyState": keyState,
            "show_keys": false,
            "username": "Username",
            "password": "Password",
            "provide_keys_form": false,
            "provide_keys": false,
            "ifCreatedKeystate": ifCreatedKeystate,
            "ifRejectedKeystate": ifRejectedKeystate,
            "ifCompletedKeystate": ifCompletedKeystate
        };

        return context;
    };
});

var generateKeys = function () {

    var tabid = $('#tabs li.active').attr("id");
    var keyType;
    if (tabid == "production-keys-tab") {
        keyType = "PRODUCTION";
    }
    else {
        keyType = "SANDBOX";
    }

    var id = document.getElementById("appid").value;

    client.default.applicationsGenerateKeysPost(
        {
            "applicationId": id,
            "Content-Type": "application/json",
            "body": {
                "validityTime": document.getElementById("validitytime").value,
                "keyType": keyType,
                "accessAllowDomains": ["ALL"],
                "callbackUrl": document.getElementById("callbackUrl").value,
                "scopes": []
                //TODO should be able to send supported grant types
            }
        }, function (data) {
            renderGeneratedKeys(data, keyType);
        }
    );
};

var renderGeneratedKeys = function (data, keyType) {

    var compiledHtml, context;

    for (var j = 0; j < Object.keys(grantTypes).length; j++) {
        if ((data.obj.callbackUrl == undefined || data.obj.callbackUrl == "" ) &&
            (grantTypes[j].key == "authorization_code" || grantTypes[j].key == "implicit")) {
            grantTypes[j].selected = false;
            grantTypes[j].disabled = true;
        } else {
            grantTypes[j].selected = true;
            grantTypes[j].disabled = false;
        }
    }
    context = {
        "callbackUrl": document.getElementById("callbackUrl").value,
        "grantTypes": grantTypes,
        "keyState": data.obj.keyState,
        "show_keys": false,
        "Key": data.obj.token.accessToken,
        "ConsumerKey": data.obj.consumerKey,
        "ConsumerSecret": data.obj.consumerSecret,
        "username": "Username",
        "password": "Password",
        "basickey": window.btoa(data.obj.consumerKey + ":" + data.obj.consumerSecret),
        "ValidityTime": data.obj.token.validityTime,
        "Scopes": "",
        "tokenScopes": data.obj.token.tokenScopes,
        "provide_keys_form": false,
        "provide_keys": false,
        "gatewayurlendpoint": "(gatewayurl)/token"

    };
    compiledHtml = AppkeyTemplate(context);
    document.getElementById(keyType.toLowerCase()).innerHTML = compiledHtml;
};

var show_Keys = function () {

    if (document.getElementById("ConsumerKey").type == 'password') {
        document.getElementById("ConsumerKey").type = 'text';
        document.getElementById("ConsumerSecret").type = 'text';
        document.getElementById("Key").type = 'text';
        document.getElementById("show_keys").childNodes[0].nodeValue = 'Hide Keys';
    }
    else {
        document.getElementById("ConsumerKey").type = 'password';
        document.getElementById("ConsumerSecret").type = 'password';
        document.getElementById("Key").type = 'password';
        document.getElementById("show_keys").childNodes[0].nodeValue = 'Show Keys';
    }
};