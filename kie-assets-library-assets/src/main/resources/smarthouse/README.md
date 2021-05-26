# Smart House
An example project generated using KIE Asset Library.

# Usage
## Package
Once you're in the project root, you can build the project using:
`mvn clean package`.

## Start
To start already built application invoke:
* Spring Boot: `java -jar smarthouse.jar`
* Quarkus: `java -jar smarthouse-runner.jar`

## Open API
* Spring Boot: http://localhost:8080/v3/api-docs
* Quarkus: http://localhost:8080/q/openapi?format=json

## Example invocation
### Input
```json
{
  "Sensors Temperature": [
    {
      "placement": "OUTSIDE",
      "current": 25,
      "previous": [
        24,23,19,16,15,11
      ]
    },
    {
      "placement": "INSIDE",
      "current": 24.9,
      "previous": [
        25,28,28,28,28,28
      ]
    }
  ],
  "Settings Temperature":
    {
      "threshold_low": 21,
      "threshold_high": 24
    },
  "Settings Humidity":
    {
      "threshold_low": 50,
      "threshold_high": 60
    }
  ,
  "Sensors Humidity": [
    {
      "placement": "OUTSIDE",
      "current": 0,
      "previous": [
        60,59,58,60,62,60
      ]
    },
    {
      "placement": "INSIDE",
      "current": 0,
      "previous": [
        50,59,58,50,52,50
      ]
    }
  ]
}
```
### CURL command
```
curl -X POST "http://0.0.0.0:8080/heating" -H  "accept: application/json" -H  "Content-Type: application/json" -d '{ "Sensors Temperature": [ { "placement": "OUTSIDE", "current": 25, "previous": [ 24,23,19,16,15,11 ] }, { "placement": "INSIDE", "current": 24.9, "previous": [ 25,28,28,28,28,28 ] } ], "Settings Temperature": { "threshold_low": 21, "threshold_high": 24 }, "Settings Humidity": { "threshold_low": 50, "threshold_high": 60 } , "Sensors Humidity": [ { "placement": "OUTSIDE", "current": 0, "previous": [ 60,59,58,60,62,60 ] }, { "placement": "INSIDE", "current": 0, "previous": [ 50,59,58,50,52,50 ] } ] }'
```
