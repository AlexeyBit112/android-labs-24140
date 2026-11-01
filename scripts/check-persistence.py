#!/usr/bin/env python3
"""Проверка данных после остановки процесса. Запускать после инструментальных тестов."""
from pathlib import Path
import re
import subprocess
import time
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]

def adb(*args):
    return subprocess.check_output(['adb', *args])

def nodes():
    adb('shell', 'uiautomator', 'dump', '/sdcard/labs-window.xml')
    return list(ET.fromstring(adb('shell', 'cat', '/sdcard/labs-window.xml')).iter('node'))

def tap(package, resource):
    matches = [n for n in nodes() if n.get('resource-id') == f'{package}:id/{resource}']
    assert len(matches) == 1, (resource, len(matches))
    x1, y1, x2, y2 = map(int, re.findall(r'\d+', matches[0].get('bounds')))
    adb('shell', 'input', 'tap', str((x1 + x2) // 2), str((y1 + y2) // 2))
    time.sleep(.5)

def restart(package):
    adb('shell', 'am', 'force-stop', package)
    adb('shell', 'am', 'start', '-n', f'{package}/.MainActivity')
    time.sleep(1)

def result(package, expected):
    found = [n.get('text') for n in nodes() if n.get('resource-id') == f'{package}:id/result']
    assert found == [expected], found

def screenshot(lab, name):
    time.sleep(3)
    path = ROOT / f'docs/screenshots/lab{lab}/{name}.png'
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(adb('exec-out', 'screencap', '-p'))

package = 'ru.kharlov.lab3'
restart(package)
tap(package, 'note')
adb('shell', 'input', 'text', 'Cold%srestart%snote')
adb('shell', 'input', 'keyevent', '4')
tap(package, 'save')
time.sleep(1)
restart(package)
tap(package, 'load')
result(package, 'Cold restart note')
screenshot(3, '14-after-process-restart')
tap(package, 'loadFile')
result(package, 'Текст для проверки ошибки')
screenshot(3, '15-file-after-process-restart')
package = 'ru.kharlov.lab4'
restart(package)
assert any(n.get('text') == 'Первая заметка' for n in nodes())
assert any(n.get('text') == 'Текст изменён через updateNote.' for n in nodes())
screenshot(4, '09-after-process-restart')
adb('shell', 'rm', '/sdcard/labs-window.xml')
print('PASS: SharedPreferences, file and SQLite survived process restart')
