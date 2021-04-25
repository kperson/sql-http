# Swift RDS Data Client 

A RDS data client for swift.

## Example

```swift
import RDSData

let client = RDSDataClient(
    secretArn: "<SECRET_ARN>",
    resourceArn: "<RESOURCE_ARN>",
    database: "<DB_NAME>",
    retryOnSleep: false
)


let firstNamesFut = client.autoCommit { tx in
    tx.executeStatement(
        sql: """
        CREATE TABLE IF NOT EXISTS person (
            id INT NOT NULL AUTO_INCREMENT,
            first_name VARCHAR(100) NOT NULL,
            last_name VARCHAR(100) NOT NULL,
            age INT NOT NULL,
            PRIMARY KEY (id)
        )
        """,
        continueAfterTimeout: true
    ).then { _ in
        tx.batchExecuteStatement(
            sql:  "INSERT INTO person (first_name, last_name, age) VALUES (:firstName, :lastName, :age)",
            paramsSet: [
                [
                    "firstName": .string("Bob"),
                    "lastName": .string("Smith"),
                    "age": .long(30)
                ],
                [
                    "firstName": .string("Susan"),
                    "lastName": .string("Smith"),
                    "age": .long(31)
                ]
            ]
        )
    }.then { _ in
        tx.executeStatement(
            sql: "SELECT first_name FROM person WHERE last_name = :lastName",
            params: [
                "lastName": .string("Smith")
            ]
        )
    }.map { result in
        result.compactMap { row in row["first_name"]?.string }
    }.then { names in
        tx.executeStatement(
            sql: "DELETE FROM person WHERE last_name = :lastName",
            params: [
                "lastName": .string("Smith")
            ]
        ).map { _ in names }
    }
}

let firstNames = try firstNamesFut.wait()
print(firstNames)

```

## Swift Package
Add the following to your `Package.swift` file.
```swift
.package(url: "https://github.com/kperson/rds-data-swift.git", .upToNextMinor(from: "1.0.0"))
```
The dependency name is `RDSData`.

## retryOnSleep

If using MySQL serverless, you may want to set `retryOnSleep` to `true` in the `RDSDataClient` initializer.  This will retry the statement if a sleep error occurs.