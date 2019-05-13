Deploy kafka
Deploy Kamel
deploy databases

Wait until kafka cluster is ready ==> XYZ

Prepare development environment
 - It needs the cluster deployed to get cert and create his-backend/src/main/resources/keystore.jks


Use botfather to create your bot
Run Telegram bot, the script runs postgres with docker just to check locally that everythig works
Find your bot and start taling to it... try /signup XYZ then /update ZYX, finally /quit

Run HIS backend in a new terminal
- runs H2 database so HIS front end will call this version also locally

Run our camel-k integration (run in OCP and connects with kafka using the non secure port 9092) in a new tab
 - should see this -==>   [Camel (camel-k) thread #2 - KafkaConsumer[hl7-events-topic]] Fetcher - [Consumer clientId=consumer-1, groupId=kafkaHisToBotConsumerGroup] Resetting offset for partition hl7-events-topic-1 to offset 0.


Run HIS front end in a new terminal
- Open browser at :4200 when you see this ==> [0] [HPM] Proxy created: [ '/server.json' ]  ->  http://localhost:8090
[0] [HPM] Proxy created: [ '/api/patients', '/api/patients/' ]  ->  http://localhost:8080
[0] 
[0] Date: 2019-05-12T15:34:38.088Z
[0] Hash: 5d47d15fac7ae494bd8f
[0] Time: 12175ms
[0] chunk {es2015-polyfills} es2015-polyfills.js, es2015-polyfills.js.map (es2015-polyfills) 284 kB [initial] [rendered]
[0] chunk {main} main.js, main.js.map (main) 35.1 kB [initial] [rendered]
[0] chunk {polyfills} polyfills.js, polyfills.js.map (polyfills) 236 kB [initial] [rendered]
[0] chunk {runtime} runtime.js, runtime.js.map (runtime) 6.08 kB [entry] [rendered]
[0] chunk {styles} styles.js, styles.js.map (styles) 345 kB [initial] [rendered]
[0] chunk {vendor} vendor.js, vendor.js.map (vendor) 7.09 MB [initial] [rendered]
[0] ℹ ｢wdm｣: Compiled successfully.


Now you can stop the telegram-bot and deploy it using 09-deploy-... you have to deploy it in order to be reachable fro the camel-k integration which is running 'in' the cluster...

You can make changes to your code in his-backend, his-frontend, integrations...

Then when you're happy just deploy it all with 10-deploy


 
