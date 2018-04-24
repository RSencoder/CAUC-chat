package login;


public class TestUserDatabase {
	public static void main(String[] args) {
		UserDatabase userDatabase = new UserDatabase();
		// 注册三个新用户
		userDatabase.insertUser("aaa", "aaa");
		userDatabase.insertUser("bbb", "bbb");
		userDatabase.insertUser("ccc", "ccc");
		// 显示所有已注册用户信息
		userDatabase.showAllUsers();
		// 用户名"ccc"，口令"ccc"是否能登录
		if (userDatabase.checkUserPassword("ccc", "ccc") == true) {
			System.out.println("用户ccc可以用口令ccc登录");
		}
		// 删除用户"bbb"
		userDatabase.deleteUser("bbb", "bbb");
		// 显示所有已注册用户信息
		userDatabase.showAllUsers();
		userDatabase.shutdownDatabase();
	}
}
