import Foundation
import SQLHttp

extension JSONEncoder {
    
    func asString<T: Encodable>(_ value: T) -> String {
        return try! String(data: encode(value), encoding: .utf8)!
    }
    
}
