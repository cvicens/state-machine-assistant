const package = require('../package.json');

const DEFAULT_BACKEND_SERVICE = 'backend';

function getConfig (fullUrl) {
    const config = {
        API_ENDPOINT: '',
        SSO_ENABLED: process.env.SSO_URL ? true : false
    };

    if (fullUrl != null) {
        let match = fullUrl.match(/(http.*\:\/\/[^\/]+)\/*/);
        console.debug(`match: ${match}`);
        if (match != null && match[1] != null) {
            console.debug(`match[1]: ${match[1]}`);
            config.API_ENDPOINT = match[1].replace(package.name, DEFAULT_BACKEND_SERVICE);  
        }
    }
    
    if (process.env.GW_ENDPOINT != null) {
        config.API_ENDPOINT = process.env.GW_ENDPOINT;
    } else if (process.env.COOLSTORE_GW_SERVICE != null) {
        config.API_ENDPOINT = process.env.COOLSTORE_GW_SERVICE + '-' + process.env.OPENSHIFT_BUILD_NAMESPACE;
    }
    
    if (process.env.SECURE_GW_ENDPOINT != null) {
        config.SECURE_API_ENDPOINT = process.env.SECURE_GW_ENDPOINT;
    } else if (process.env.SECURE_GW_SERVICE != null) {
        config.SECURE_API_ENDPOINT = process.env.SECURE_GW_SERVICE + '-' + process.env.OPENSHIFT_BUILD_NAMESPACE;
    }

    console.log(`config: ${JSON.stringify(config)}`);

    return config;
}


module.exports = getConfig;