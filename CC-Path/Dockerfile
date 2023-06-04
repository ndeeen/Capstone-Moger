# 
FROM python:3.9

EXPOSE 8080

# 
WORKDIR /app

# 
COPY requirements.txt .
RUN python -m pip install -r requirements.txt
# 
COPY . /app

# 
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8080", "--reload"]