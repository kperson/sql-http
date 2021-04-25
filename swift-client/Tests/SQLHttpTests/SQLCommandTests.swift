import Foundation
import XCTest

@testable import SQLHttp

class SQLCommandTests: XCTestCase {

    let encoder = SQLHttpClient.encoder
    let decoder = SQLHttpClient.decoder
    
    func testEncodingQuery() {
        let val = SQLCommand.query(.init(dataSourceReference: .custom("test"), sql: "SELECT 1"))
        let valJSON = encoder.asString(val)
        let expectedJSON = "{\"data\":{\"dataSourceReference\":{\"data\":\"test\",\"dataType\":\"Custom\"},\"params\":{},\"sql\":\"SELECT 1\"},\"dataType\":\"Query\"}"
        XCTAssertEqual(valJSON, expectedJSON)
        let command = try! decoder.decode(SQLCommand.self, from: expectedJSON.data(using: .utf8)!)
        XCTAssertEqual(command, val)
    }
    
    func testEncodingWrite() {
        let val = SQLCommand.write(.init(dataSourceReference: .custom("test"), sql: "UPDATE table SET row = 1"))
        let valJSON = encoder.asString(val)
        let expectedJSON = "{\"data\":{\"dataSourceReference\":{\"data\":\"test\",\"dataType\":\"Custom\"},\"params\":{},\"sql\":\"UPDATE table SET row = 1\"},\"dataType\":\"Write\"}"
        XCTAssertEqual(valJSON, expectedJSON)
        let command = try! decoder.decode(SQLCommand.self, from: expectedJSON.data(using: .utf8)!)
        XCTAssertEqual(command, val)
    }
    
    func testEncodingBatchWrite() {
        let val = SQLCommand.batchWrite(.init(dataSourceReference: .custom("test"), sql: "UPDATE table SET row = 1"))
        let valJSON = encoder.asString(val)
        let expectedJSON = "{\"data\":{\"dataSourceReference\":{\"data\":\"test\",\"dataType\":\"Custom\"},\"params\":[],\"sql\":\"UPDATE table SET row = 1\"},\"dataType\":\"BatchWrite\"}"
        XCTAssertEqual(valJSON, expectedJSON)
        let command = try! decoder.decode(SQLCommand.self, from: expectedJSON.data(using: .utf8)!)
        XCTAssertEqual(command, val)
    }
    
}
