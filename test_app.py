from flask import Flask, render_template, send_from_directory
import os

app = Flask(__name__)

@app.route('/')
def index():
    return send_from_directory('.', 'test.html')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=12001, debug=True)