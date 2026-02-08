# 🗄️ Command-Driven In-Memory Database

## 📋 Project Overview
A **thread-safe, TTL-enabled, generic in-memory database** implemented in Java with a command-line interface. This project implements all phases of a mini-project specification with full multi-threading support and automatic expiration.

**MAX MARKS: 100** | **Mini Project Submission**

## ✨ Features Implemented

### ✅ **All Project Phases Completed:**
- **Phase 1**: Command Parser with input validation
- **Phase 2**: Basic In-Memory Database with HashMap
- **Phase 3**: TTL (Time-To-Live) support for automatic expiration
- **Phase 4**: Lazy expiration on GET operations
- **Phase 5**: Multi-threaded concurrency support
- **Phase 6**: Thread safety using synchronized methods
- **Phase 7**: Database lifecycle control (STOP/START)
- **Phase 8**: Background TTL cleanup thread
- **Phase 9**: Custom exception handling framework
- **Phase 10**: Performance upgrade with ConcurrentHashMap
- **Phase 11**: Final deliverables with complete documentation

### 🔧 **Technical Features:**
- **Thread-safe operations** using ConcurrentHashMap
- **TTL expiration** with background cleanup thread
- **Command-driven interface** with real-time parsing
- **Multi-threaded command execution** (3 executor threads)
- **Database state management** using volatile variables
- **Generic data storage** (String type implementation)
- **Comprehensive exception handling** with custom exceptions
- **Automatic memory management** for expired entries

## 🚀 How to Run

### **Prerequisites:**
- Java JDK 8 or higher
- Git (optional, for cloning)

### **Quick Start:**
#### **Windows Users:**
```cmd
# Simply double-click:
run.bat
