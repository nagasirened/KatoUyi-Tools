namespace java com.katouyi.tools.thriftServices

//  thrift --gen java ./test_thrift.thrift

service UserService {

    /** 根据id查看某个用户 */
    User getUserById(1:i64 id),

    /** 新增一个用户 */
    bool addUser(1:User user),

    /** 获取所有用户 */
    list<User> getAllUsers(),

    /** 测试遍历枚举 */
    map<string, Numberz> getAllEnums()

}

// 枚举
enum Numberz {
    ONE = 1,
    TWO = 2,
    THREE = 3,
    FIVE = 5,
    SIX= 6,
    EIGHT = 8
}

// 定义class对象类
struct User {
    1:i64 id,
    2:string username,
    3:i16 age
}