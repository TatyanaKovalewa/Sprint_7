#!/usr/bin/env python3
"""Сверяет упавшие тесты со списком известных дефектов стенда.

Сборка падает только на падениях вне списка .github/known-stand-defects.txt.
Тесты, прошедшие с перезапуска (flaky), падением не считаются.
"""
import glob
import os
import sys
import xml.etree.ElementTree as ET

BASELINE = ".github/known-stand-defects.txt"


def load_baseline():
    with open(BASELINE, encoding="utf-8") as f:
        return {
            line.strip()
            for line in f
            if line.strip() and not line.lstrip().startswith("#")
        }


def base_name(name):
    """Отбрасывает суффикс с параметрами: Test#method[Кнопка: true, ...] -> Test#method."""
    return name.split("[", 1)[0]


def collect_failures():
    failed, flaky = set(), set()
    for path in sorted(glob.glob("target/surefire-reports/TEST-*.xml")):
        for case in ET.parse(path).getroot().iter("testcase"):
            name = "{}#{}".format(
                case.get("classname", "?").split(".")[-1], case.get("name", "?")
            )
            if case.find("failure") is not None or case.find("error") is not None:
                failed.add(name)
            elif (
                case.find("flakyFailure") is not None
                or case.find("flakyError") is not None
            ):
                flaky.add(name)
    return failed, flaky


def main():
    known = load_baseline()
    failed, flaky = collect_failures()

    # Строка списка может задавать как конкретный прогон параметризованного теста,
    # так и весь метод целиком — тогда она покрывает все его параметризации.
    def is_known(name):
        return name in known or base_name(name) in known

    unexpected = sorted(n for n in failed if not is_known(n))
    expected = sorted(n for n in failed if is_known(n))
    still_failing = {base_name(n) for n in failed} | failed
    fixed = sorted(k for k in known if k not in still_failing)

    out = ["### Сверка с известными дефектами стенда", ""]
    if expected:
        out.append(f"🟡 **Ожидаемые падения — дефекты стенда ({len(expected)}):**")
        out += [f"- `{n}`" for n in expected] + [""]
    if flaky:
        out.append(f"🔁 **Прошли с перезапуска (флаки) ({len(flaky)}):**")
        out += [f"- `{n}`" for n in flaky] + [""]
    if fixed:
        out.append(f"✅ **Стенд починили — убрать из списка ({len(fixed)}):**")
        out += [f"- `{n}`" for n in fixed] + [""]
    if unexpected:
        out.append(f"🔴 **Новые падения — требуют разбора ({len(unexpected)}):**")
        out += [f"- `{n}`" for n in unexpected] + [""]
    if not unexpected:
        out.append("Новых падений нет.")

    text = "\n".join(out)
    print(text)
    summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary:
        with open(summary, "a", encoding="utf-8") as f:
            f.write(text + "\n")

    return 1 if unexpected else 0


if __name__ == "__main__":
    sys.exit(main())
