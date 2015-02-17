(ns caas.jwt
  (:require [buddy.core.keys :as keys]
            [buddy.sign.jws :as jws]))

;; create key instances
(def ec-privkey (keys/private-key "ecprivkey.pem"))

(def ec-pubkey (keys/public-key "ecpubkey.pem"))

(defn sign [value]
  (jws/sign value ec-privkey {:alg :es256}))

(defn unsign [value]
  (jws/unsign value ec-pubkey {:alg :es256}))
