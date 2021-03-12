import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';

/// @describe
/// @author  XQ Yang
/// @date 12/25/2018  10:09 AM
@deprecated
class FavoriteDao {
  Database mDb;
  static FavoriteDao dao;

  static FavoriteDao get() {
    if (dao == null) {
      dao = FavoriteDao();
    }
    return dao;
  }

  Future<Database> getDb() async {
    if (mDb == null) {
      var databasesPath = await getDatabasesPath();
      String path = join(databasesPath, 'fPix.db');
      // open the database
      mDb = await openDatabase(path, version: 1,
          onCreate: (Database db, int version) async {
        // When creating the db, create the table
        await db.execute(
            'CREATE TABLE FavoriteTable (id INTEGER PRIMARY KEY,imgId INTEGER,tags TEXT,pageURL TEXT,largeImageURL TEXT)');
      });
    }
    return mDb;
  }

  void closeDb() async {
    await mDb.close();
  }


  Future<List<Map<String, dynamic>>> getAllImgUrl() async {
    var database = await getDb();
     var list = await database.query('FavoriteTable');
    List<Map<String, dynamic>> result = new List();
     list.forEach((item){
       Map<String, dynamic> map = new Map();
       item.forEach((k,v){
         map[k] = v;
       });
       map['id']=item['imgId'];
       result.add(map);
     });
     return result;
  }

  Future<int> insert(int id,String tags,String pageURL, String largeImageURL) {
    return getDb().then((db) {
      return db.transaction((txn) async {
        int resultId = await txn.rawInsert(
            'INSERT INTO FavoriteTable (imgId,tags,pageURL, largeImageURL) '
            'VALUES (?,?,?,?)',
            [id,tags,pageURL, largeImageURL]);
        print('inserted: $resultId');
        return resultId;
      });
    });
    // Insert some records in a transaction
  }

  Future<int> deleteFid(int id) async {
    return await getDb().then((db) {
      return db.transaction((txn) async {
        int count = await txn
            .rawDelete('DELETE FROM FavoriteTable WHERE imgId = ?', [id]);
        print('delete Count : $count ');
        return count;
      });
    });
    // Insert some records in a transaction
  }

  Future<int> deleteFurl(String largeImageURL) async {
    return await getDb().then((db) {
      db.transaction((txn) async {
        int count = await txn.rawDelete(
            'DELETE FROM FavoriteTable WHERE largeImageURL = ?',
            [largeImageURL]);
        print('delete Count : $count ');
        return count;
      });
    });
    // Insert some records in a transaction
  }

  Future<bool> contains(int id) async {
    List list = await getDb().then((db) {
      return db.query('FavoriteTable',
          columns: ['id'], where: "imgId=?", whereArgs: [id]);
    });
//    print('contains : ${list.isNotEmpty}');
    return list.isNotEmpty;
  }
}
