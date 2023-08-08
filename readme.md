## A Simple Multithreaded JSON-Database

The database stores key-value-pairs. Values are represented as JSON documents.
Multiple clients can connect to the database over the network via sockets.

Commands sent to the database must follow the following syntax:

### Examples
#### Save a new object (file)
```json 
{
  "type":"set",
  "key":"person",
  "value":{
    "name":"Elon Musk",
    "car":{
      "model":"Tesla Roadster",
      "year":"2018"
    },
    "rocket":{
      "name":"Falcon 9",
      "launches":"87"
    }
  }
}
```

#### Set a nested object of an existing object
```json 
{
"type":"set",
"key":["person", "hyperloop", "hyperloop1"],
"value":{
    "name":"Hyperloop 1",
    "speed": 1000
    }
}
```

#### Get a nested object (commandline args)
```json
{
  "type":"get",
  "key":["person", "hyperloop", "hyperloop1"]
}
```

#### Delete an object
```json
{
  "type":"delete",
  "key":"person"
}
```

#### Delete a nested object
```json
{
  "type":"delete",
  "key":["person", "hyperloop"]
}
```


