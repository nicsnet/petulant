set :stage, :newt

role :app, 'caas0.newt.int.crealytics.local'
role :web, 'caas0.newt.int.crealytics.local'
role :db,  'caas0.newt.int.crealytics.local'

server 'caas0.newt.int.crealytics.local', user: 'apps', roles: %w{web app db}, ssh_options: { forward_agent: true }

set :clj_env, :integration
