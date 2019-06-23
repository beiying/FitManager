//
// Created by beiying on 19/5/1.
//

#include <iostream>
#include <cmath>
#include <cassert>
#include <ctime>

using namespace std;

template<typename T>
int binarySearch(T arr[], int n, T target) {
    int l = 0,r = n - 1;//在[l,n]区间寻找target
    while(l <= r) {//边界情况是l==r; 需要维护好循环不变量
        int mid = (l + r) / 2;
        if (arr[mid] == target) {
            return mid;
        }
        if (arr[mid] > target) {
            r = mid + 1;
        }
        if (arr[mid] < target) {
            l = mid + 1;
        }
    }
    return -1;
}

int main() {

}
