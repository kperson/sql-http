import Foundation
import HttpExecuter



public class SQLHttpClient {
    
    static let calendar = Calendar(identifier: .iso8601)
    
    private static func createFormatter() -> DateFormatter {
        let formatter = DateFormatter()
        formatter.calendar = calendar
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"
        return formatter
    }
    
    private static func createEncoder() -> JSONEncoder {
        let encoder = JSONEncoder()
        encoder.dataEncodingStrategy = .base64
        encoder.dateEncodingStrategy = .formatted(dateFormatter)
        return encoder
    }
    
    private static func createDecoder() -> JSONDecoder {
        let decoder = JSONDecoder()
        decoder.dataDecodingStrategy = .base64
        decoder.dateDecodingStrategy = .formatted(dateFormatter)
        return decoder
    }
    
    public static let dateFormatter = createFormatter()
    public static let encoder = createEncoder()
    public static let decoder = createDecoder()
    
    public let endpoint: String
    public let executer: RequestExecuter
    public let dataSourceReference: DataSourceReference
    public let middleWare: [RequestMiddleWare] = []
    

    public init(
        endpoint: String,
        dataSourceReference: DataSourceReference,
        executer: RequestExecuter = DefaultRequestExecuter(),
        middleWare: [RequestMiddleWare] = []
    ) {
        self.endpoint = endpoint
        self.executer = executer
        self.dataSourceReference = dataSourceReference
    }
    
    
    public func query(_ sql: String) -> QueryBuilder {
        return QueryBuilder(client: self, sql: sql)
    }
    
    
    public func write(_ sql: String) -> WriteBuilder {
        return WriteBuilder(client: self, sql: sql)
    }
    
    
    public func executeQuery(sql: String, params: [String: SQLValue], callback: @escaping ([Row]?, Error?) -> Void) {
        let query = Query(dataSourceReference: dataSourceReference, sql: sql, params: params)
        executeCommand(command: SQLCommand.query(query)) { response, error in
            if let r = response {
                do {
                    let rows = try Self.decoder.decode(QueryResponse.self, from: r.body).rows
                    callback(rows, nil)
                }
                catch let error {
                    callback(nil, error)
                }
            }
            else if let e = error {
                callback(nil, e)
            }
        }
    }
    
    public func executeWrite(sql: String, params: [String: SQLValue], callback: @escaping (WriteResponse?, Error?) -> Void) {
        let query = Write(dataSourceReference: dataSourceReference, sql: sql, params: params)
        executeCommand(command: SQLCommand.write(query)) { response, error in
            if let r = response {
                do {
                    let rows = try Self.decoder.decode(WriteResponse.self, from: r.body)
                    callback(rows, nil)
                }
                catch let error {
                    callback(nil, error)
                }
            }
            else if let e = error {
                callback(nil, e)
            }
        }
    }
    
    private func executeCommand(command: SQLCommand, callback: @escaping (HttpResponse?, Error?) -> Void) {
        do {
            let data = try Self.encoder.encode(command)
            let url = URL(string: endpoint)!
            let request = HttpRequest(requestMethod: .POST, url: url, body: data, headers: [:])
            executer.execute(request: request) { (response, error) in
                callback(response, error)
            }
        }
        catch let error {
            callback(nil, error)
        }
        
    }

}
