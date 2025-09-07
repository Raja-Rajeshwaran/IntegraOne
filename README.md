# 🏢 IntegraOne ERP - Enterprise Resource Planning System

A comprehensive Java Swing-based ERP system designed to streamline business operations through integrated modules for inventory, finance, sales, HR, and customer management with a user-friendly desktop interface.

## 🚀 Core Modules

### 📦 Inventory Management
- **Real-time Stock Tracking** with low-level alerts
- **Multi-location Warehouse Management**
- **Barcode/QR Code Integration**
- **Supplier Management** with performance tracking
- **Product Categories & Variants**
- **Stock Transfer & Adjustment**

### 💰 Financial Management
- **General Ledger** with double-entry bookkeeping
- **Accounts Payable/Receivable**
- **Multi-currency Support**
- **Bank Reconciliation**
- **Tax Management** (GST/VAT)
- **Financial Reporting** (P&L, Balance Sheet)

### 👥 Customer Relationship Management (CRM)
- **Customer Database** with contact history
- **Lead Management** & opportunity tracking
- **Sales Pipeline** visualization
- **Customer Support** ticket system
- **Email Integration** for notifications
- **Customer Analytics** & insights

### 📊 Sales & Purchase Management
- **Sales Order Processing** with workflow
- **Purchase Order Management**
- **Quotation & Invoice Generation**
- **Payment Tracking** with multiple methods
- **Sales Analytics** & forecasting
- **Commission Management**

### ��‍💼 Human Resources
- **Employee Database** with profiles
- **Attendance Management** with time tracking
- **Payroll Processing** with tax calculations
- **Leave Management** with approval workflow
- **Performance Reviews**
- **Training & Development** tracking

### 📈 Reporting & Analytics
- **Real-time Dashboards** with KPIs
- **Custom Report Builder**
- **Data Visualization** (charts, graphs)
- **Export to PDF/Excel**
- **Scheduled Reports** via email
- **Business Intelligence** insights

## 🛠️ Technology Stack

### Backend
- **Language:** Java 8+
- **GUI Framework:** Java Swing
- **Database:** MySQL 8.0+
- **Database Driver:** MySQL Connector/J
- **Architecture:** MVC (Model-View-Controller)
- **Build Tool:** Maven/Gradle

### Database
- **RDBMS:** MySQL Server
- **Connection Pooling:** HikariCP
- **ORM:** Custom DAO pattern
- **Migrations:** SQL scripts

### Additional Libraries
- **Charts:** JFreeChart for data visualization
- **PDF Generation:** iText or Apache PDFBox
- **Excel Export:** Apache POI
- **Date/Time:** Java 8 Time API
- **Logging:** Log4j or SLF4J


## ⚙️ Installation & Setup

### Prerequisites
- Java JDK 8 or higher
- MySQL Server 8.0 or higher
- MySQL Connector/J (JDBC driver)

### Step 1: Database Setup
1. Start your MySQL server
2. Create the database:
   ```sql
   CREATE DATABASE integraone_erp;
   USE integraone_erp;
   ```
3. Import the schema:
   ```bash
   mysql -u root -p integraone_erp < src/main/resources/sql/schema.sql
   mysql -u root -p integraone_erp < src/main/resources/sql/initial_data.sql
   ```

### Step 2: Configure Database Connection
Edit `src/main/resources/properties/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/integraone_erp
db.username=your_username
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
```

### Step 3: Add Required Libraries
Download and add to `lib/` directory:
- `mysql-connector-java.jar`
- `jfreechart.jar` (for charts)
- `itextpdf.jar` (for PDF generation)
- `poi.jar` (for Excel export)
- `log4j.jar` (for logging)

### Step 4: Compile and Run
```bash
# Using Maven
mvn clean compile
mvn exec:java -Dexec.mainClass="com.integraone.main.Main"

# Using Java directly
javac -cp "lib/*;src" src/main/java/com/integraone/main/Main.java
java -cp "lib/*;src" com.integraone.main.Main
```

## 🔑 Default Credentials

| Role | Username | Password | Access Level |
|------|----------|----------|--------------|
| Super Admin | admin | admin123 | Full system access |
| Manager | manager | manager123 | Department-level access |
| Employee | employee | employee123 | Limited access |
| Accountant | accountant | acc123 | Financial modules only |

## 🎯 Key Features

### 🔐 Security & Access Control
- **User Authentication** with password hashing
- **Role-based Access Control** (RBAC)
- **Session Management** with timeout
- **Audit Logging** for all actions
- **Data Validation** and sanitization

### 🖥️ User Interface
- **Modern Java Swing** interface with custom styling
- **Responsive Layout** that adapts to screen size
- **Dark/Light Theme** toggle
- **Keyboard Shortcuts** for power users
- **Multi-window Support** for complex workflows

### 📊 Data Management
- **Real-time Data Sync** with MySQL
- **Offline Capability** for critical functions
- **Data Import/Export** (CSV, Excel, PDF)
- **Backup & Restore** functionality
- **Data Validation** and integrity checks

## �� Dashboard Overview

### Executive Dashboard
- **Revenue Trends** with year-over-year comparison
- **Top Products** by sales volume
- **Customer Acquisition** metrics
- **Cash Flow** visualization
- **Key Performance Indicators** (KPIs)

### Operational Dashboard
- **Inventory Levels** with low-stock alerts
- **Pending Orders** and shipments
- **Employee Attendance** overview
- **Customer Support** tickets status
- **System Health** monitoring

## �� Configuration

### Application Properties
```properties
# src/main/resources/properties/application.properties
app.name=IntegraOne ERP
app.version=1.0.0
app.theme=light
app.language=en
app.timezone=UTC
app.currency=USD
```

### Database Configuration
```properties
# src/main/resources/properties/database.properties
db.url=jdbc:mysql://localhost:3306/integraone_erp
db.username=root
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
db.pool.size=10
db.timeout=30
```

## 🚀 Deployment

### Development
```bash
# Run in development mode
mvn clean compile
mvn exec:java -Dexec.mainClass="com.integraone.main.Main"
```

### Production
```bash
# Create JAR file
mvn clean package

# Run the JAR
java -jar target/integraone-erp-1.0.0.jar
```

### Distribution
```bash
# Create executable JAR with dependencies
mvn clean package assembly:single

# Create installer (optional)
# Use Launch4j or similar tool to create .exe
```

## �� Reporting Features

### Financial Reports
- **Profit & Loss Statement**
- **Balance Sheet**
- **Cash Flow Statement**
- **Trial Balance**
- **General Ledger**

### Operational Reports
- **Sales Report** by period/product/customer
- **Inventory Report** with stock levels
- **Employee Report** with attendance
- **Customer Report** with purchase history
- **Supplier Report** with performance metrics

### Export Options
- **PDF Export** with professional formatting
- **Excel Export** for data analysis
- **CSV Export** for external systems
- **Email Reports** with scheduling

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify MySQL server is running
   - Check database credentials in `database.properties`
   - Ensure database exists and is accessible
   - Verify MySQL Connector/J is in classpath

2. **GUI Not Displaying Properly**
   - Check Java version compatibility
   - Verify Look and Feel settings
   - Check screen resolution settings
   - Ensure all required libraries are loaded

3. **Performance Issues**
   - Monitor database query performance
   - Check memory usage and JVM settings
   - Review application logs for bottlenecks
   - Optimize database indexes

4. **Report Generation Errors**
   - Verify PDF/Excel libraries are in classpath
   - Check file write permissions
   - Ensure sufficient disk space
   - Review report template configurations

## 📝 Development

### Code Standards
- **Java:** Follow Oracle Java Code Conventions
- **Swing:** Use consistent naming for UI components
- **Database:** Use prepared statements for all queries
- **Testing:** Write unit tests for business logic

### Adding New Features
1. Create model class in `model/` package
2. Add DAO class in `dao/` package
3. Create service class in `service/` package
4. Design GUI in `gui/` package
5. Update main menu to include new module
6. Test thoroughly with different user roles

### Best Practices
- **MVC Pattern:** Separate model, view, and controller
- **Exception Handling:** Proper error handling and user feedback
- **Data Validation:** Client and server-side validation
- **Logging:** Comprehensive logging for debugging
- **Documentation:** Comment complex business logic

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## ��‍�� Team

**IntegraOne ERP Development Team**

- **Project Lead:** [Your Name]
- **Backend Developers:** [Team Members]
- **UI/UX Developers:** [Team Members]
- **Database Administrators:** [Team Members]
- **QA Engineers:** [Team Members]

---

**🏢 Transform your business operations with IntegraOne ERP! 🚀**
