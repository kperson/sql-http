import Foundation
import HttpExecuter


public enum RequestMiddleAction {
    
    case pass
    case addHeaders([String : String])
    case reject(reason: String)

}

public protocol RequestMiddleWare {
    
    func modify(action: @escaping (RequestMiddleAction?) -> Void)
    
}
