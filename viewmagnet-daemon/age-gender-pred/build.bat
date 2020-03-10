@ECHO OFF
ECHO batch file executed successfully.
cd %1 && conda activate && python %1\realtime_pred.py
PAUSE