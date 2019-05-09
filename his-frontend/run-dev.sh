#export GW_ENDPOINT=http://istio-ingressgateway-istio-system.apps.serverless-8a7c.openshiftworkshop.com

export OPENSHIFT_BUILD_NAMESPACE=`oc project -q`
export HIS_FRONTEND_CUSTOM_PORT=8090

npm run dev