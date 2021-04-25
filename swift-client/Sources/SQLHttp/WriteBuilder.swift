import Foundation


public class WriteBuilder {
    
    public let client: SQLHttpClient
    public let sql: String
    public private(set) var params: [String : SQLValue] = [:]
    
    
    public init(client: SQLHttpClient, sql: String) {
        self.client = client
        self.sql = sql
    }
    
    public func addParam(_ name: String, _ value: SQLValue) -> WriteBuilder {
        params[name] = value
        return self
    }
    
    public func addParams(_ values: [String: SQLValue]) -> WriteBuilder {
        for (k, v) in values {
            _ = addParam(k, v)
        }
        return self
    }
    
    public func execute(_ callback: @escaping (WriteResponse?, Error?) -> Void) {
        client.executeWrite(sql: sql, params: params, callback: callback)
    }
    
}
