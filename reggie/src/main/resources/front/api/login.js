function loginApi(data={}) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

function sendEmailApi(data) {
    return $axios({
        'url': '/user/sendEmail',
        'method': 'post',
        data
    })
}
