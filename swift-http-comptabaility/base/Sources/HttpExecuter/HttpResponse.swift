import Foundation

public struct HttpResponse {
    
    public let statusCode: UInt
    public let body: Data
    public let headers: Dictionary<String, String>
    
    public init(statusCode: UInt, body: Data, headers: Dictionary<String, String>) {
        self.statusCode = statusCode
        self.body = body
        self.headers = headers
    }
    
}

public extension HttpResponse {
    
    var bodyAsText: String {
        return String(data: body, encoding: .utf8) ?? ""
    }
    
}
