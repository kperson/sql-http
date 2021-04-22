import Foundation
import HttpExecuter
import SwiftBasicHTTP
import NIO


public class NIO1RequestExecuter: RequestExecuter {
    
    let client: HttpClient
    
    init(client: HttpClient = HttpClient.shared) {
        self.client = client
    }
    
    public func execute(
        request: HttpExecuter.HttpRequest,
        _ callback: @escaping (HttpExecuter.HttpResponse?, Error?) -> Void
    ) {
        let nativeRequest = SwiftBasicHTTP.HttpRequest(
            requestMethod: SwiftBasicHTTP.RequestMethod.init(rawValue: request.requestMethod.rawValue)!,
            url: request.url,
            body: request.body,
            headers: request.headers
        )
        let f = client.runRequest(request: nativeRequest)
        f.whenSuccess {
            let response = HttpExecuter.HttpResponse(statusCode: $0.statusCode, body: $0.body, headers: $0.headers)
            callback(response, nil)
        }
        f.whenFailure {
            callback(nil, $0)
        }
    }
}

public extension HttpExecuter.HttpRequest {
    
    func future(executer: RequestExecuter) -> EventLoopFuture<HttpExecuter.HttpResponse> {
        let p = HttpClient.shared.eventGroup.next().newPromise(of: HttpExecuter.HttpResponse.self)
        execute(executer) { (response, error) in
            if let h = response {
                p.succeed(result: h)
            }
            else if let e = error {
                p.fail(error: e)
            }
        }
        return p.futureResult
    }
    
}
