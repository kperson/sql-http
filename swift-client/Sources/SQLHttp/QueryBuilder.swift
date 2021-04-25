import Foundation


public class QueryBuilder {
    
    public let client: SQLHttpClient
    public let sql: String
    public private(set) var params: [String : SQLValue] = [:]
    
    
    public init(client: SQLHttpClient, sql: String) {
        self.client = client
        self.sql = sql
    }
    
    public func addParam(_ name: String, _ value: SQLValue) -> QueryBuilder {
        params[name] = value
        return self
    }
    
    public func addParams(_ values: [String: SQLValue]) -> QueryBuilder {
        for (k, v) in values {
            _ = addParam(k, v)
        }
        return self
    }
    
    public func execute(_ callback: @escaping ([Row]?, Error?) -> Void) {
        client.executeQuery(sql: sql, params: params, callback: callback)
    }
    
}
