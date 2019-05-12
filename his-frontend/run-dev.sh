#export GW_ENDPOINT=http://his-backend-health-assitant.apps.serverless-0428.openshiftworkshop.com
#export GW_ENDPOINT=http://localhost:8080

export OPENSHIFT_BUILD_NAMESPACE=`oc project -q`
export HIS_FRONTEND_CUSTOM_PORT=8090

npm run dev