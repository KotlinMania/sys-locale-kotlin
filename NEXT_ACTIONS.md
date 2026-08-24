# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 1/7 (14.3%)
- **Function parity:** 3/21 matched (target 3) — 14.3%
- **Class/type parity:** 0/15 matched (target 1) — 0.0%
- **Combined symbol parity:** 3/36 matched (target 4) — 8.3%
- **Average inline-code cosine:** 0.34 (function body across 1 matched files)
- **Average documentation cosine:** 0.82 (doc text across 1 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 1 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `syslocale.Lib`
- **Similarity:** 0.34
- **Dependents:** 0
- **Priority Score:** 10406.6
- **Functions:** 3/4 matched (target 3)
- **Missing functions:** `get`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 1/1 matched

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

