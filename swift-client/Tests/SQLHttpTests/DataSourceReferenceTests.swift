import Foundation
import XCTest

@testable import SQLHttp



class DataSourceReferenceTests: XCTestCase {
    
    let encoder = SQLHttpClient.encoder
    let decoder = SQLHttpClient.decoder
    
    override func setUp() {
        super.setUp()
        encoder.outputFormatting = .sortedKeys
    }
    
    func testDirect() {
        let direct = DataSourceReference.direct(
            .init(
                jdbcURL: "jdbc:mysql://localhost:3306/mydb",
                credentials: .init(username: "username", password: .init("secret.password")),
                properties: ["hello": "world"]
            )
        )
        let json = encoder.asString(direct)
        let expectedJson = "{\"data\":{\"credentials\":{\"password\":\"secret.password\",\"username\":\"username\"},\"jdbcURL\":\"jdbc:mysql:\\/\\/localhost:3306\\/mydb\",\"properties\":{\"hello\":\"world\"}},\"dataType\":\"Direct\"}"
        XCTAssertEqual(json, expectedJson)
    }
    
    func testCustom() {
        let custom = DataSourceReference.custom("lookup.value")
        let json = encoder.asString(custom)
        let expectedJson = "{\"data\":\"lookup.value\",\"dataType\":\"Custom\"}"
        XCTAssertEqual(json, expectedJson)
    }
    
    
}
