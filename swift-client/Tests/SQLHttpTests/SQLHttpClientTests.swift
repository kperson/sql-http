import Foundation


import Foundation
import HttpExecuter
import XCTest

@testable import SQLHttp

class SQLHttpClientTests: XCTestCase {
    
    let client = SQLHttpClient(
        endpoint: "http://localhost:8081/",
        dataSourceReference: .direct(
            .init(
                jdbcURL: "jdbc:mysql://sql_http_mysql:3306/test_db",
               credentials: .init(username: "test_user", password: .init("test_password"))
            )
        )
    )
    
    override func setUp() {
        super.setUp()
        let lock = NSLock()
        lock.lock()
        self.client.write(
            "DROP TABLE IF EXISTS test_table"
        ).execute { _, _ in
            self.client.write(
                """
                CREATE TABLE `test_table` (
                    `first_name` VARCHAR(255) NOT NULL,
                    `age` INT NOT NULL, `created_at`
                    DATETIME(6) NOT NULL
                )
                """
            ).execute { _, _ in
                lock.unlock()
            }
        }
        lock.lock()
    }
    
    
    func testWriteAndRead() {
        let exp = expectation(description: "write and read")
        let now = Date()
        client.write(
            """
            INSERT INTO test_table (
                first_name,
                age,
                created_at
            ) VALUES (
                :first_name,
                :age,
                :created_at
            )
            """
        )
        .addParam("first_name", .string("Bob"))
        .addParam("age", .int(22))
        .addParam("created_at", .date(now))
        .execute { rs, _ in
            self.client.query(
                "SELECT * FROM test_table WHERE first_name = :first_name"
            )
            .addParam("first_name", .string("Bob"))
            .execute { rows, _ in
                XCTAssertEqual(rows![0]["first_name"].string, "Bob")
                XCTAssertEqual(rows![0]["age"].int, 22)
                let createdAtEpoch = rows![0]["created_at"].date.timeIntervalSince1970
                XCTAssertTrue(abs(now.timeIntervalSince1970 - createdAtEpoch) < 0.01)
                exp.fulfill()
            }
        }
        waitForExpectations(timeout: 5)
    }
    
    
}
