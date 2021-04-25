import Foundation


public struct Row: Codable {
    
    public let columns: [Column]
    
    public init(columns: [Column]) {
        self.columns = columns
    }
    
    public subscript(string: String) -> Column {
        return columns.first { $0.label.lowercased() == string.lowercased() }!
    }
    
    public subscript(int: Int) -> Column {
        return columns.first { $0.index == int }!
    }
    
}
