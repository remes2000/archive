using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;


namespace IotDataStore.Models
{
    public class IotDataStoreContext : DbContext
    {
        public IotDataStoreContext(DbContextOptions<IotDataStoreContext> options) : base(options) { }

        public DbSet<User> Users {get; set;}
        public DbSet<Project> Projects { get; set; }
        public DbSet<Table> Tables { get; set; }
        public DbSet<Measurement> Measurements { get; set; }

        protected override void OnModelCreating(ModelBuilder builder)
        {
            builder.Entity<Table>().Property(t => t.TableItemModel).HasColumnType("json");
            builder.Entity<Measurement>().Property(m => m.Data).HasColumnType("json");
        }
    }
}
