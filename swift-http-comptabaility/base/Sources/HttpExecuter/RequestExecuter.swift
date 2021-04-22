import Foundation


public protocol RequestExecuter {
    
    func execute(request: HttpRequest, _ callback: @escaping (HttpResponse?, Error?) -> Void)
    
}

public extension HttpRequest {
    
    func execute(_ executer: RequestExecuter, _ callback: @escaping (HttpResponse?, Error?) -> Void) {
        executer.execute(request: self, callback)
    }
    
}
