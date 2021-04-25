import Foundation
import XCTest

@testable import SQLHttp

class SQLValueTests: XCTestCase {

    let encoder = JSONEncoder()
    let decoder = JSONDecoder()
    
    override func setUp() {
        super.setUp()
        encoder.outputFormatting = .sortedKeys
        encoder.dataEncodingStrategy = .base64
        decoder.dataDecodingStrategy = .base64
        let formatter = DateFormatter()
        formatter.calendar = Calendar(identifier: .iso8601)
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"
        decoder.dateDecodingStrategy = .formatted(formatter)
        encoder.dateEncodingStrategy = .formatted(formatter)
    }
    
    func testEncodingString() {
        let val = SQLValue.string("hello")
        let valJSON = encoder.asString(val)
        let expectedJSON = "{\"data\":\"hello\",\"dataType\":\"String\"}"
        XCTAssertEqual(valJSON, expectedJSON)
        
        let primitive = try! decoder.decode(SQLValue.self, from: expectedJSON.data(using: .utf8)!)
        XCTAssertEqual(primitive, val)
    }
    
    func testEncodingLong() {
        let val = SQLValue.long(123)
        let valJSON = encoder.asString(val)
        let expectedJSON = "{\"data\":123,\"dataType\":\"Long\"}"
        XCTAssertEqual(valJSON, expectedJSON)
        
        let primitive = try! decoder.decode(SQLValue.self, from: expectedJSON.data(using: .utf8)!)
        XCTAssertEqual(primitive, val)
    }
    
    func testEncodingInt() {
        let val = SQLValue.int(321)
        let valJSON = encoder.asString(val)
        let expectedJSON = "{\"data\":321,\"dataType\":\"Long\"}"
        XCTAssertEqual(valJSON, expectedJSON)
        
        let primitive = try! decoder.decode(SQLValue.self, from: expectedJSON.data(using: .utf8)!)
        XCTAssertEqual(primitive, val)
    }

    func testEncodingBlob() {
        let val = SQLValue.data("hello world".data(using: .utf8)!)
        let valJSON = encoder.asString(val)
        let expectedJSON = "{\"data\":\"aGVsbG8gd29ybGQ=\",\"dataType\":\"Blob\"}"
        XCTAssertEqual(valJSON, expectedJSON)
        
        let primitive = try! decoder.decode(SQLValue.self, from: expectedJSON.data(using: .utf8)!)
        XCTAssertEqual(primitive, val)
    }
    
    func testEncodingDate() {
        let val = SQLValue.date(Date(timeIntervalSince1970: 3600))
        let valJSON = encoder.asString(val)
        print(valJSON)
        let expectedJSON = "{\"data\":\"1970-01-01T01:00:00.000\",\"dataType\":\"Date\"}"
        XCTAssertEqual(valJSON, expectedJSON)
        let primitive = try! decoder.decode(SQLValue.self, from: expectedJSON.data(using: .utf8)!)
        XCTAssertEqual(primitive, val)
    }
    
    func testEncodingTime() {
        let val = SQLValue.time(5324.24)
        let valJSON = encoder.asString(val)
        print(valJSON)
        let expectedJSON = "{\"data\":5324240,\"dataType\":\"Time\"}"
        XCTAssertEqual(valJSON, expectedJSON)
        let primitive = try! decoder.decode(SQLValue.self, from: expectedJSON.data(using: .utf8)!)
        XCTAssertEqual(primitive, val)
    }
    
    func testEncodingNull() {
        let val = SQLValue.null
        let valJSON = encoder.asString(val)
        let expectedJSON = "{\"dataType\":\"Null\"}"
        XCTAssertEqual(valJSON, expectedJSON)
        
        let primitive = try! decoder.decode(SQLValue.self, from: expectedJSON.data(using: .utf8)!)
        XCTAssertEqual(primitive, val)
    }
    
}

