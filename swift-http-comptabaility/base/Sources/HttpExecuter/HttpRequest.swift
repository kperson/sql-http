import Foundation


public enum RequestMethod : String {
    
    case POST = "POST"
    case GET = "GET"
    case DELETE = "DELETE"
    case PUT = "PUT"
    case OPTIONS = "OPTIONS"
    case CONNECT = "CONNECT"
    case TRACE = "TRACE"
    case HEAD = "HEAD"
    case PATCH = "PATCH"
    
}


public struct HttpRequest {
    
    public let requestMethod: RequestMethod
    public let url: URL
    public let body: Data?
    public let headers: Dictionary<String, String>?
    
    public init(
        requestMethod: RequestMethod,
        url: URL,
        body: Data?,
        headers: Dictionary<String, String>?
    ) {
        self.requestMethod = requestMethod
        self.url = url
        self.body = body
        self.headers = headers
    }
    
}
