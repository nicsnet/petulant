# config valid only for current version of Capistrano
lock '3.3.5'

set :application, 'CAAS'
set :deploy_user, 'apps'
set :scm, :git
set :branch, (ENV['TAG'] || "master")
set :repo_url, 'git@github.com:crealytics/caas.git'
set :keep_releases, 5

set :format, :pretty
set :deploy_to, '/srv/caas'

set :log_level, :debug

set :default_stage, "newt"

# Default value for :linked_files is []
# set :linked_files, fetch(:linked_files, []).push('config/database.yml')

set :linked_dirs, %w{ log }


after 'deploy:publishing', 'deploy:restart'
namespace :deploy do

  task :restart do
    # something
  end

  after :restart, :clear_cache do
    on roles(:web), in: :groups, limit: 3, wait: 10 do
      # Here we can do anything such as:
      # within release_path do
      #   execute :rake, 'cache:clear'
      # end
    end
  end
end

# TODO: Remove comment when config files are setup.
# before 'deploy:updated', 'deploy:fetch_configs'
# namespace :deploy do
#   desc 'Sets up sensitive config data for CAAS.'
#   task :fetch_configs do
#     run_locally do
#       execute "rm -rf /tmp/caas_configs/#{fetch(:stage)} && mkdir -p /tmp/caas_configs/#{fetch(:stage)}"
#       execute 'git clone ' + "git@gitlab.crealytics.com:cap/caas-#{fetch(:stage)}-configs.git" + " " + "/tmp/caas_configs/#{fetch(:stage)}/config"
#       execute "rm -rf /tmp/caas_configs/#{fetch(:stage)}/config/.git"
#     end
#     on roles(:app) do
#       execute "mkdir -p #{File.join(shared_path, 'config')}"
#       upload!("/tmp/caas_configs/#{fetch(:stage)}/config", shared_path, recursive: true)
#     end
#     run_locally do
#       execute "rm -rf /tmp/caas_configs/#{fetch(:stage)}"
#     end
#   end
# end


# TODO: Remove comment when config files are setup.
#before 'deploy:updated', 'db:symlink'
# namespace :db do
#   # setup the database credentials via symlink
#   # see http://www.simonecarletti.com/blog/2009/06/capistrano-and-database-yml/
#   desc <<-DESC
#     [internal] Updates the symlink for database.yml to the just deployed release.
#   DESC
#   task :symlink do
#     on roles(:app) do
#       execute "ln -nfs #{shared_path}/config/database.yml #{release_path}/config/database.yml"
#       execute "ln -nfs #{shared_path}/config/secrets.yml #{release_path}/config/secrets.yml"
#       execute "ln -nfs #{shared_path}/config/newrelic.yml #{release_path}/config/newrelic.yml"
#     end
#   end
# end

