import Foundation
import XCTest

@testable import SQLHttp



class MaskedTests: XCTestCase {
    
    struct TestValueWrapper<T> {
        let value: T
    }

    let encoder = SQLHttpClient.encoder
    let decoder = SQLHttpClient.decoder
    
    
    func testMaskedEncoding() {
        let masked = Masked("hello")
        let json = encoder.asString(masked)
        let expectedJson = "\"hello\""
        XCTAssertEqual(json, expectedJson)
        let decoded = try! decoder.decode(Masked.self, from: expectedJson.data(using: .utf8)!)
        XCTAssertEqual(decoded, masked)
    }
    
    func testRepresentation() {
        let rawPassword = "super.secret.password"
        let password = Masked(rawPassword)
        let description = String(describing: password)
        XCTAssertFalse(description.contains(rawPassword), "descriptions must not contain masked value")
        let nestedDescription = String(describing: TestValueWrapper(value: password))
        XCTAssertFalse(nestedDescription.contains(rawPassword), "descriptions must not contain masked value")
    }
    
    
}
