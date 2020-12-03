
import 'package:flutter/material.dart';

class CurrentPageModel with ChangeNotifier {
  int _currentPageIndex = 0;

  int get value => _currentPageIndex;

  void change(int index) {
    if (index >= 0 && index <= 3 && _currentPageIndex != index) {
      _currentPageIndex = index;
      notifyListeners();
    }
  }
}