import Foundation
import HttpExecuter
import AsyncHTTPClient
import NIOHTTP1
import NIO


public class NIO2RequestExecuter: RequestExecuter {
    
    let client: HTTPClient
    
    public static let group = MultiThreadedEventLoopGroup(numberOfThreads: System.coreCount)
    
    init(eventLoopGroupProvider: HTTPClient.EventLoopGroupProvider = .createNew) {
        self.client = HTTPClient(eventLoopGroupProvider: eventLoopGroupProvider)
    }
    
    public func execute(
        request: HttpExecuter.HttpRequest,
        _ callback: @escaping (HttpExecuter.HttpResponse?, Error?) -> Void
    ) {
        
        var method: NIOHTTP1.HTTPMethod = .GET
        switch request.requestMethod {
        case .POST: method = NIOHTTP1.HTTPMethod.POST
        case .GET: method = NIOHTTP1.HTTPMethod.GET
        case .DELETE: method = NIOHTTP1.HTTPMethod.DELETE
        case .PUT: method = NIOHTTP1.HTTPMethod.PUT
        case .OPTIONS: method = NIOHTTP1.HTTPMethod.OPTIONS
        case .CONNECT: method = NIOHTTP1.HTTPMethod.CONNECT
        case .TRACE: method = NIOHTTP1.HTTPMethod.CONNECT
        case .HEAD: method = NIOHTTP1.HTTPMethod.CONNECT
        case .PATCH: method = NIOHTTP1.HTTPMethod.CONNECT
        }
        do {
            let headers = request.headers?.map { (k, v) -> (String, String) in
                (k, v)
            } ?? []
    
            var body: HTTPClient.Body? = nil
            if let b = request.body, !b.isEmpty {
                body = HTTPClient.Body.data(b)
            }
        
            let request = try HTTPClient.Request(
                url: request.url.absoluteString,
                method: method,
                headers: .init(headers),
                body: body
            )
            
            let f = client.execute(request: request)
            
            f.whenSuccess {
                var h: [String : String] = [:]
                $0.headers.forEach {
                    h[$0.name] = $0.value
                }
                let data = $0.body.map { b in Data(buffer: b) } ?? Data()
                
                let res = HttpExecuter.HttpResponse(statusCode: $0.status.code, body: data, headers: h)
                callback(res, nil)
            }
            
            f.whenFailure {
                callback(nil, $0)
            }
        }
        catch let error {
            callback(nil, error)
        }
        
    }
}

public extension HttpExecuter.HttpRequest {

    func future(executer: RequestExecuter) -> EventLoopFuture<HttpExecuter.HttpResponse> {
        let p = NIO2RequestExecuter.group.next().makePromise(of: HttpExecuter.HttpResponse.self)
        execute(executer) { (response, error) in
            if let h = response {
                p.succeed(h)
            }
            else if let e = error {
                p.fail(e)
            }
        }
        return p.futureResult
    }

}
